package de.muenchen.refarch.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionLimitingSuccessHandler
        extends RedirectServerAuthenticationSuccessHandler
        implements ServerAuthenticationSuccessHandler {

    private final ReactiveSessionStore sessionStore; // eigene Abstraktion
    private final int maxSessions = 1;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
            Authentication authentication) {
        log.info("Authentication success");

        ServerWebExchange exchange = webFilterExchange.getExchange();
        return super.onAuthenticationSuccess(webFilterExchange, authentication)
                .then(exchange.getSession().flatMap(session -> sessionStore.registerSession(authentication.getName(), session, maxSessions)));
        //        return exchange.getSession()
        //                .flatMap(session ->
        //                        sessionStore
        //                                .registerSession(authentication.getName(), session, maxSessions)
        //                                .then(Mono.defer(() -> webFilterExchange.getChain().filter(exchange)))
        //                ).then(super.onAuthenticationSuccess(webFilterExchange, authentication));
    }
}
