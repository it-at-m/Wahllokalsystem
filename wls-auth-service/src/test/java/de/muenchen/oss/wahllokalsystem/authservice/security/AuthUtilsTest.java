package de.muenchen.oss.wahllokalsystem.authservice.security;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.val;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthUtilsTest {

    @Nested
    class GetUsername {

        @Nested
        class WithAuthenticationParameter {

            @Test
            void should_returnUserName_when_jwtAuthenticationTokenIsGiven() {
                val username = "username";
                val authentication = new JwtAuthenticationToken(
                        new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("key", "value"), Map.of("user_name", username)));

                val result = AuthUtils.getUsername(authentication);
                Assertions.assertThat(result).isEqualTo(username);
            }

            @Test
            void should_returnUsername_when_usernamePasswordAuthenticationToken() {
                val username = "username";
                val authentication = new UsernamePasswordAuthenticationToken("username", "password");

                val result = AuthUtils.getUsername(authentication);
                Assertions.assertThat(result).isEqualTo(username);
            }

            @Test
            void should_returnNameForUnauthenticatedUser_when_authenticationTokenIsNotSupported() {
                val result = AuthUtils.getUsername(new Authentication() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return List.of();
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public Object getPrincipal() {
                        return null;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return false;
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                    }

                    @Override
                    public String getName() {
                        return "";
                    }
                });
                Assertions.assertThat(result).isEqualTo("unauthenticated");
            }

            @Test
            void should_returnNameForUnauthenticatedUser_when_authenticationIsNull() {
                Assertions.assertThat(AuthUtils.getUsername(null)).isEqualTo("unauthenticated");
            }
        }

        @Nested
        class WithoutAuthenticationParameter {

            @Test
            void should_returnUsername_when_authenticationIsGivenInSecurityContext() {
                val username = "username";
                val authentication = new UsernamePasswordAuthenticationToken("username", "password");

                SecurityContextHolder.getContext().setAuthentication(authentication);

                val result = AuthUtils.getUsername();
                Assertions.assertThat(result).isEqualTo(username);

                SecurityContextHolder.clearContext();
            }

            @Test
            void should_returnNameForUnauthenticatedUser_when_authenticationIsNotGivenInSecurityContext() {
                SecurityContextHolder.clearContext();

                val result = AuthUtils.getUsername();
                Assertions.assertThat(result).isEqualTo("unauthenticated");
            }
        }
    }
}