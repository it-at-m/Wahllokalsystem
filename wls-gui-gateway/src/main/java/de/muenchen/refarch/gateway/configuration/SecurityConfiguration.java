package de.muenchen.refarch.gateway.configuration;

import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@Profile("!no-security")
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CsrfProtectionMatcher csrfProtectionMatcher;
    private final SessionProperties sessionProperties;
    private final ServerProperties serverProperties;

    @Bean
    @Order(0)
    public SecurityWebFilterChain clientAccessFilterChain(final ServerHttpSecurity http) {
        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/clients/**"))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.OPTIONS, "/clients/**").permitAll()
                        .anyExchange().authenticated())
                .cors(corsSpec -> {
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        val logoutHandler = new DelegatingServerLogoutHandler(
                new SecurityContextServerLogoutHandler(), new WebSessionServerLogoutHandler()
        );

        http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        // permitAll
                        .pathMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .pathMatchers("/api/*/actuator/info",
                                "/actuator/health",
                                "/actuator/health/liveness",
                                "/actuator/health/readiness",
                                "/actuator/info",
                                "/actuator/metrics",
                                "/actuator/sbom",
                                "/actuator/sbom/application",
                                "/public/**")
                        .permitAll()
                        // only authenticated
                        .anyExchange().authenticated())
                .logout(spec -> spec.logoutUrl("/logout").logoutHandler(new ServerLogoutHandler() {
                    @Override
                    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
                        log.info("logout request received");
                        return logoutHandler.logout(exchange, authentication);
                    }
                }).logoutSuccessHandler((exchange, authentication) -> {
                    log.info("logout successful");
                    exchange.getExchange().getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                    exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("http://kubernetes.docker.internal:8100/logout"));

                    return exchange.getExchange().getResponse().setComplete();
                }))
                .cors(corsSpec -> {
                })
                .csrf(csrfSpec -> {
                    /*
                     * Custom csrf request handler for spa and BREACH attack protection.
                     * https://docs.spring.io/spring-security/reference/6.1-SNAPSHOT/servlet/exploits/csrf.html#csrf-
                     * integration-javascript-spa
                     */
                    csrfSpec.csrfTokenRequestHandler(new SpaServerCsrfTokenRequestHandler());
                    /*
                     * The necessary subscription for csrf token attachment to {@link ServerHttpResponse}
                     * is done in class {@link CsrfTokenAppendingHelperFilter}.
                     */
                    csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
                    csrfSpec.requireCsrfProtectionMatcher(csrfProtectionMatcher);
                })
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler() {
                    @Override
                    public Mono<Void> onAuthenticationSuccess(final WebFilterExchange webFilterExchange, final Authentication authentication) {
                        webFilterExchange.getExchange().getSession().subscribe(
                                webSession -> webSession.setMaxIdleTime(getSessionTimeout()));
                        return super.onAuthenticationSuccess(webFilterExchange, authentication);
                    }
                }));

        return http.build();
    }

    /**
     * Get Spring Session timeout. Uses {@link SessionProperties} and {@link ServerProperties#getServlet()} as fallback, like Spring Session itself. See
     * according
     * <a href="https://docs.spring.io/spring-boot/reference/web/spring-session.html">Spring
     * documentation</a>.
     *
     * @return Spring session timeout.
     */
    protected Duration getSessionTimeout() {
        return sessionProperties.determineTimeout(() -> serverProperties.getServlet().getSession().getTimeout());
    }
}
