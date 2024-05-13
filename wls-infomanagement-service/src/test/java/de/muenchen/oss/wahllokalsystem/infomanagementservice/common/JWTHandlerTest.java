package de.muenchen.oss.wahllokalsystem.infomanagementservice.common;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class JWTHandlerTest {

    private final JWTHandler unitUnderTest = new JWTHandler();

    @Nested
    class GetDetail {

        @Test
        void detailKeyHasData() {
            val detailKey = "requestedKey";
            val detailValue = "detailValue";

            val jwt = createJWT(Map.of(detailKey, detailValue));

            val expectedResult = Optional.of(detailValue);

            val result = unitUnderTest.getDetail(detailKey, new JwtAuthenticationToken(jwt));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void detailKeyHasNoData() {
            val detailKey = "requestedKey";

            val jwt = createJWT(Map.of(detailKey + "extra", detailKey));

            val result = unitUnderTest.getDetail(detailKey, new JwtAuthenticationToken(jwt));

            Assertions.assertThat(result).isEmpty();
        }

        private Jwt createJWT(final Map<String, Object> claims) {
            return new Jwt("tokenValue", Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), Map.of("key1", "value1"), claims);
        }
    }

}
