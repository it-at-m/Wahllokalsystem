package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.SecurityUtils;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class KonfigurationServiceSecurityTest {

    private final String TESTUSER = "testuser";
    private final String TESTUSER_PASSWORD = "secret";

    @MockBean
    JWTHandler jwtHandler;

    @Autowired
    KonfigurationService konfigurationService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetKonfiguration {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_GET_KONFIGURATION);

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getKonfiguration(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> konfigurationService.getKonfiguration(KonfigurationKonfigKey.WILLKOMMENSTEXT))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            val requiredAuthorities = Authorities.ALL_AUTHORITIES_GET_KONFIGURATION;
            return Arrays.stream(requiredAuthorities)
                    .map(authorityToRemove ->
                            //remove one authority from all required authorities
                            Arguments.of(Arrays.stream(requiredAuthorities)
                                    .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
        }
    }
}
