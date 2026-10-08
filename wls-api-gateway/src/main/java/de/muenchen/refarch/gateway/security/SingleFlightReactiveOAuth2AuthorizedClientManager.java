package de.muenchen.refarch.gateway.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import reactor.core.publisher.Mono;

/**
 * Ensures that concurrent requests for one principal and client registration share a single
 * authorization operation. This prevents parallel refresh-token requests from invalidating each
 * other when the identity provider rotates refresh tokens.
 */
public final class SingleFlightReactiveOAuth2AuthorizedClientManager implements ReactiveOAuth2AuthorizedClientManager {

    private final ReactiveOAuth2AuthorizedClientManager delegate;
    private final OAuth2AuthorizationCoordinator authorizationCoordinator;
    private final ConcurrentMap<String, Mono<OAuth2AuthorizedClient>> authorizationsInProgress = new ConcurrentHashMap<>();

    public SingleFlightReactiveOAuth2AuthorizedClientManager(
            final ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        this(authorizedClientManager, new OAuth2AuthorizationCoordinator() {
            @Override
            public <T> Mono<T> execute(final String key, final Supplier<Mono<T>> operation) {
                return operation.get();
            }
        });
    }

    public SingleFlightReactiveOAuth2AuthorizedClientManager(
            final ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
            final OAuth2AuthorizationCoordinator authorizationCoordinator) {
        this.delegate = authorizedClientManager;
        this.authorizationCoordinator = authorizationCoordinator;
    }

    @Override
    public Mono<OAuth2AuthorizedClient> authorize(final OAuth2AuthorizeRequest authorizeRequest) {
        final String key = authorizeRequest.getPrincipal().getName()
                + ":"
                + authorizeRequest.getClientRegistrationId();

        return Mono.defer(() -> authorizationsInProgress.computeIfAbsent(
                key,
                ignored -> authorizeOnce(key, authorizeRequest)));
    }

    private Mono<OAuth2AuthorizedClient> authorizeOnce(
            final String key,
            final OAuth2AuthorizeRequest authorizeRequest) {
        return authorizationCoordinator.execute(key, () -> delegate.authorize(authorizeRequest))
                .doFinally(signalType -> authorizationsInProgress.remove(key))
                .cache();
    }
}
