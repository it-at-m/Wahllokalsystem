package de.muenchen.refarch.gateway.filter;

import de.muenchen.refarch.gateway.security.ReactiveSessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SessionValidityFilter implements WebFilter {

    private final ReactiveSessionStore sessionStore;

    public SessionValidityFilter(ReactiveSessionStore sessionStore) {
        log.info("Initializing SessionValidityFilter");
        this.sessionStore = sessionStore;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Trigger to check session validity");
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> exchange.getSession()
                        .flatMap(session -> sessionStore.isValid(auth.getName(), session.getId())
                                .flatMap(valid -> {
                                    if (!valid) {
                                        return session.invalidate()
                                                .then(Mono.defer(() -> {
                                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                                    return exchange.getResponse().setComplete();
                                                }));
                                    }
                                    return chain.filter(exchange);
                                })
                        )
                )
                // kein Principal → nicht eingeloggt → immer gültig
                .switchIfEmpty(chain.filter(exchange));
    }

}
