/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Profile("no-security")
@EnableWebSecurity
public class NoSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        // @formatter:off
        http
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(requests -> requests.requestMatchers(AntPathRequestMatcher.antMatcher("/**"))
                        .permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest()
                        .permitAll())
                .sessionManagement(customizer ->
                        customizer.sessionFixation().migrateSession()
                                .maximumSessions(1)
                                .expiredUrl("/login")
                                .maxSessionsPreventsLogin(false)
                                .sessionRegistry(sessionRegistry)
                )
                .csrf(AbstractHttpConfigurer::disable);
        // @formatter:on
        return http.build();
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
