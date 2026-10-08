package de.muenchen.refarch.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SingleFlightReactiveOAuth2AuthorizedClientManagerTest {

    @Nested
    class Authorize {
        @Test
        void should_shareAuthorization_when_requestsForSamePrincipalAndRegistrationAreParallel() {
            final AtomicInteger authorizationCalls = new AtomicInteger();
            final OAuth2AuthorizedClient authorizedClient = createAuthorizedClient();
            final SingleFlightReactiveOAuth2AuthorizedClientManager manager = new SingleFlightReactiveOAuth2AuthorizedClientManager(
                    request -> Mono.defer(() -> {
                        authorizationCalls.incrementAndGet();
                        return Mono.delay(java.time.Duration.ofMillis(50)).thenReturn(authorizedClient);
                    }));
            final OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId("sso")
                    .principal(new TestingAuthenticationToken("user", null))
                    .build();

            final int completedAuthorizations = Flux.range(0, 10)
                    .flatMap(ignored -> manager.authorize(request))
                    .collectList()
                    .block()
                    .size();

            assertEquals(1, authorizationCalls.get());
            assertEquals(10, completedAuthorizations);
        }
    }

    private OAuth2AuthorizedClient createAuthorizedClient() {
        final ClientRegistration registration = ClientRegistration.withRegistrationId("sso")
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://example.org/login/oauth2/code/sso")
                .authorizationUri("https://example.org/authorize")
                .tokenUri("https://example.org/token")
                .build();
        final OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token",
                Instant.now(),
                Instant.now().plusSeconds(60));

        return new OAuth2AuthorizedClient(registration, "user", accessToken);
    }
}
