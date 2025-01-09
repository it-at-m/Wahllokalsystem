package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class JWTDetailExtractorTest {

    private final JWTDetailExtractor unitUnderTest = new JWTDetailExtractor();

    @Nested
    class CanHandle {

        @Test
        void should_throwIllegalArgumentException_when_authenticationIsNull() {
            Assertions.assertThatThrownBy(() -> unitUnderTest.canHandle(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_returnTrue_when_authenticationIsJwtAuthenticationToken() {
            val detailKey = "requestedKey";
            val detailValue = "detailValue";
            val jwt = createJWT(Map.of(detailKey, detailValue));

            Assertions.assertThat(unitUnderTest.canHandle(new JwtAuthenticationToken(jwt))).isTrue();
        }

        @Test
        void should_returnTrue_when_authenticationSubclassOfJwtAuthenticationToken() {
            val detailKey = "requestedKey";
            val detailValue = "detailValue";
            val jwt = createJWT(Map.of(detailKey, detailValue));

            Assertions.assertThat(unitUnderTest.canHandle(new JwtAuthenticationToken(jwt) {
            })).isTrue();
        }

        @Test
        void should_returnFalse_when_authenticationIsNotJwtAuthenticationToken() {
            Assertions.assertThat(unitUnderTest.canHandle(new AbstractAuthenticationToken(Collections.emptyList()) {
                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }
            })).isFalse();
        }
    }

    @Nested
    class GetDetail {

        @Test
        void should_returnEmptyOptional_when_authenticationIsNotInstanceOfJwtAuthenticationToken() {
            val result = unitUnderTest.getDetail("key", new UsernamePasswordAuthenticationToken("principal", "credentials"));

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void should_returnValues_when_claimWithKeyExists() {
            val detailKey = "requestedKey";
            val detailValue = "detailValue";

            val jwt = createJWT(Map.of(detailKey, detailValue));

            val expectedResult = Optional.of(detailValue);

            val result = unitUnderTest.getDetail(detailKey, new JwtAuthenticationToken(jwt));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnValues_when_claimWithKeyDoesNotExists() {
            val detailKey = "requestedKey";

            val jwt = createJWT(Map.of(detailKey + "extra", detailKey));

            val result = unitUnderTest.getDetail(detailKey, new JwtAuthenticationToken(jwt));

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void should_throwsIllegalArgumentException_when_keyIsNull() {
            val detailValue = "detailValue";

            val jwt = createJWT(Map.of("detailKey", detailValue));

            Assertions.assertThatThrownBy(() -> unitUnderTest.getDetail(null, new JwtAuthenticationToken(jwt))).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwsIllegalArgumentException_when_authenticationIsNull() {
            Assertions.assertThatThrownBy(() -> unitUnderTest.getDetail("key", null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    private Jwt createJWT(final Map<String, Object> claims) {
        return new Jwt("tokenValue", Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), Map.of("key1", "value1"), claims);
    }

}
