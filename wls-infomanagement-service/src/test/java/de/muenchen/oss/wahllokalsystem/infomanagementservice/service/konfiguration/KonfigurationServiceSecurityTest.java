package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
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

    @Autowired
    KonfigurationService konfigurationService;

    @MockBean
    KonfigurationModelValidator konfigurationModelValidator;

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

    @Nested
    class SetKonfiguration {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_SET_KONFIGURATION);

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel));
        }

        @Test
        void accessDeniedOnMissingServiceAuthority() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD,
                    removeAuthority(Authorities.ALL_AUTHORITIES_SET_KONFIGURATION, Authorities.SERVICE_POST_KONFIGURATION));

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnMissingWriteAuthority() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD,
                    removeAuthority(Authorities.ALL_AUTHORITIES_SET_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION));

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }

        private String[] removeAuthority(final String[] authorities, final String authorityToRemove) {
            return Arrays.stream(authorities).filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new);
        }
    }

    @Nested
    class GetAllKonfigurations {

        @Test
        void accessGranted() {

            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_GET_KONFIGURATIONS);

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getAllKonfigurations());
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> konfigurationService.getAllKonfigurations())
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            val requiredAuthorities = Authorities.ALL_AUTHORITIES_GET_KONFIGURATIONS;
            return Arrays.stream(requiredAuthorities)
                    .map(authorityToRemove ->
                            //remove one authority from all required authorities
                            Arguments.of(Arrays.stream(requiredAuthorities)
                                    .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
        }
    }
}
