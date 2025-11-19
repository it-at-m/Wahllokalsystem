/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.properties.RSAConfigurationProperties;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.properties.RSAKeySetting;
import de.muenchen.oss.wahllokalsystem.authservice.security.CustomUsernamePasswordAuthenticationFilter;
import de.muenchen.oss.wahllokalsystem.authservice.security.WlsUserTokenCustomizer;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * The central class for configuration of all security aspects.
 */
@Configuration
@Profile("!no-security")
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Import(RestTemplateAutoConfiguration.class)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final String LOGIN_PATH = "/login";

    @Autowired
    private CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter;

    @Autowired
    private UserService userService;

    @Value("${service.config.oauth2.jwk.rsa.init.seed}")
    String rsaKeyPairSeed;

    private final RSAConfigurationProperties rsaConfigurationProperties;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint(LOGIN_PATH),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()))
                .logout(logoutspec -> logoutspec.clearAuthentication(true));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests.requestMatchers(
                        // allow access to /actuator/info
                        PathPatternRequestMatcher.withDefaults().matcher("/actuator/info"),
                        // allow access to /actuator/health for OpenShift Health Check
                        PathPatternRequestMatcher.withDefaults().matcher("/actuator/health"),
                        // allow access to /actuator/health/liveness for OpenShift Liveness Check
                        PathPatternRequestMatcher.withDefaults().matcher("/actuator/health/liveness"),
                        // allow access to /actuator/health/readiness for OpenShift Readiness Check
                        PathPatternRequestMatcher.withDefaults().matcher("/actuator/health/readiness"),
                        // allow access to /actuator/metrics for Prometheus monitoring in OpenShift
                        PathPatternRequestMatcher.withDefaults().matcher("/actuator/metrics"),
                        PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
                        PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
                        PathPatternRequestMatcher.withDefaults().matcher("/"),
                        PathPatternRequestMatcher.withDefaults().matcher("/home"),
                        PathPatternRequestMatcher.withDefaults().matcher("/css/*"),
                        PathPatternRequestMatcher.withDefaults().matcher("/js/*"),
                        PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new JwtUserInfoAuthenticationConverter(userService))))
                .formLogin((form) -> form
                        .loginPage(LOGIN_PATH)
                        .permitAll())
                .logout(LogoutConfigurer::permitAll)
                .logout(logoutspec -> logoutspec.permitAll(true).clearAuthentication(true)
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults()
                                .matcher(HttpMethod.GET, "/logout"))
                        .logoutSuccessHandler(
                                (request, response, authentication) -> log.info("logout successful for {}", authentication.getName())))
                .securityContext(securityContext -> securityContext.requireExplicitSave(false))
                .addFilterBefore(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(final JdbcOperations jdbcOperations) {
        return new JdbcRegisteredClientRepository(jdbcOperations);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        Pair<RSAPrivateKey, RSAPublicKey> keyPair = getKeyPair();
        val rsaKey = new RSAKey.Builder(keyPair.getValue())
                .privateKey(keyPair.getKey())
                .keyID("keyID")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private Pair<RSAPrivateKey, RSAPublicKey> getKeyPair() {
        RSAPrivateKey privateKey;
        RSAPublicKey publicKey;
        if (rsaConfigurationProperties.getRsaKeySetting() == RSAKeySetting.STATIC_KEY) {
            publicKey = rsaConfigurationProperties.getPublicKey();
            privateKey = rsaConfigurationProperties.getPrivateKey();
        } else if (rsaConfigurationProperties.getRsaKeySetting() == RSAKeySetting.GENERATED_KEY) {
            log.warn("""
                    ##############################
                    ##                          ##
                    ##  Generierte RSA-Keys     ##
                    ##      werden verwendet    ##
                    ##                          ##
                    ##  NICHT FÜR DEN           ##
                    ##  PRODUKTIVEINSATZ        ##
                    ##  BESTIMMT                ##
                    ##############################""");

            val keyPair = generateRsaKey();
            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } else {
            throw new IllegalStateException("RSA settings not supported");
        }
        return Pair.of(privateKey, publicKey);
    }

    private KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, new SecureRandom(rsaKeyPairSeed.getBytes()));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy(SessionRegistry sessionRegistry) {
        return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
    }

    @Bean
    public WlsUserTokenCustomizer wlsUserTokenCustomizer(final UserService userService) {
        return new WlsUserTokenCustomizer(userService);
    }
}
