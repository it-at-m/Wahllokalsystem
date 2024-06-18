package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NotFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)

@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WahlvorstandServiceSecurityTest {

    @Autowired
    WahlvorstandService wahlvorstandService;

    @Nested
    class GetWahlvorstandForWahlbezirk {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GETWAHLVORSTANDFORWAHLBEZIRK);

            Assertions.assertThatException().isThrownBy(() -> wahlvorstandService.getWahlvorstandForWahlbezirk(UUID.randomUUID().toString())).isInstanceOf(
                    NotFoundException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> wahlvorstandService.getWahlvorstandForWahlbezirk(UUID.randomUUID().toString()))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GETWAHLVORSTANDFORWAHLBEZIRK);
        }
    }

    @Nested
    class SetAnwesenheit {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORIRITES_SETANWESENHEIT);

            val aktualisierung = new WahlvorstandsaktualisierungDTO(UUID.randomUUID().toString(),
                    Set.of(new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false)), LocalDateTime.now());

            Assertions.assertThatException().isThrownBy(() -> wahlvorstandService.setAnwesenheit(aktualisierung)).isInstanceOf(NotFoundException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val aktualisierung = new WahlvorstandsaktualisierungDTO(UUID.randomUUID().toString(),
                    Set.of(new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false)), LocalDateTime.now());

            Assertions.assertThatThrownBy(() -> wahlvorstandService.setAnwesenheit(aktualisierung))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORIRITES_SETANWESENHEIT);
        }

    }

}
