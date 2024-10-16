package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    KonfigurationService konfigurationService;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @MockBean
    KonfigurationModelValidator konfigurationModelValidator;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_KONFIGURATION);
        konfigurationRepository.deleteAll();
    }

    @Nested
    class GetKonfiguration {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_KONFIGURATION);

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getKonfiguration(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> konfigurationService.getKonfiguration(KonfigurationKonfigKey.WILLKOMMENSTEXT))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_KONFIGURATION);
        }
    }

    @Nested
    class SetKonfiguration {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_KONFIGURATION);

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel));
        }

        @Test
        void accessDeniedOnMissingServiceAuthority() {
            SecurityUtils.runWith(
                    removeAuthority(Authorities.ALL_AUTHORITIES_SET_KONFIGURATION, Authorities.SERVICE_POST_KONFIGURATION));

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnMissingWriteAuthority() {
            SecurityUtils.runWith(
                    removeAuthority(Authorities.ALL_AUTHORITIES_SET_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION));

            val konfigurationSetModel = new KonfigurationSetModel("schluessel", "wert", "beschreibung", "standwert");

            Assertions.assertThatThrownBy(() -> konfigurationService.setKonfiguration(konfigurationSetModel))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        private String[] removeAuthority(final String[] authorities, final String authorityToRemove) {
            return Arrays.stream(authorities).filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new);
        }
    }

    @Nested
    class GetAllKonfigurations {

        @Test
        void accessGranted() {

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_KONFIGURATIONS);

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getAllKonfigurations());
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> konfigurationService.getAllKonfigurations())
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_KONFIGURATIONS);
        }
    }

    @Nested
    class GetKennbuchstabenListen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_KONFIGURATION);
            konfigurationRepository.save(new Konfiguration("KENNBUCHSTABEN", "", "", ""));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_KENNBUCHSTABEN_LISTEN);

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getKennbuchstabenListen());
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_KONFIGURATION);
            konfigurationRepository.save(new Konfiguration("KENNBUCHSTABEN", "", "", ""));

            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> konfigurationService.getKennbuchstabenListen())
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_KENNBUCHSTABEN_LISTEN);
        }
    }

    @Nested
    class GetKonfigurationUnauthorized {
        @Test
        void accessGranted() {
            SecurityContextHolder.clearContext();

            Assertions.assertThatNoException().isThrownBy(() -> konfigurationService.getKonfigurationUnauthorized(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }
    }
}
