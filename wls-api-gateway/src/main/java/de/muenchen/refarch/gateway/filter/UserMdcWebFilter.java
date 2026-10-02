package de.muenchen.refarch.gateway.filter;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UserMdcWebFilter implements WebFilter {

    private static final Logger LOG = LoggerFactory.getLogger(UserMdcWebFilter.class);
    private static final String MDC_USER_KEY = "user";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .defaultIfEmpty(null)
                .flatMap(auth -> {
                    String username = resolveUsername(auth);
                    LOG.debug("UserMdcWebFilter: resolved username={}", username);

                    // best-effort: set/remove MDC for reactive signals of this request
                    return chain.filter(exchange)
                            .doOnEach(signal -> {
                                if ((signal.isOnNext() || signal.isOnComplete() || signal.isOnError()) && username != null) {
                                    MDC.put(MDC_USER_KEY, username);
                                }
                            })
                            .doFinally(sig -> MDC.remove(MDC_USER_KEY));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String resolveUsername(Authentication auth) {
        if (auth == null) {
            LOG.debug("Auth ist null");
            return null;
        }

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            if (jwt != null) {
                for (String claim : new String[] { "preferred_username", "username", "upn", "email", "sub" }) {
                    String v = jwt.getClaimAsString(claim);
                    if (v != null && !v.isBlank()) return v;
                }
            }
            if (jwtAuth.getName() != null && !jwtAuth.getName().isBlank()) return jwtAuth.getName();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof OAuth2User oauth) {
            Object pref = oauth.getAttribute("preferred_username");
            if (pref != null) return pref.toString();
            Object email = oauth.getAttribute("email");
            if (email != null) return email.toString();
            if (oauth.getName() != null && !oauth.getName().isBlank()) return oauth.getName();
        }
        if (principal instanceof Principal p) {
            return p.getName();
        }
        String name = auth.getName();
        return (name == null || name.isBlank()) ? null : name;
    }
}
