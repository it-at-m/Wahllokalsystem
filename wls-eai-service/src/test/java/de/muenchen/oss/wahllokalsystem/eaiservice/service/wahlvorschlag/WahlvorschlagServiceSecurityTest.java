package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NoSearchResultFoundException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.stream.Stream;
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
public class WahlvorschlagServiceSecurityTest {

    @Autowired
    WahlvorschlagService wahlvorschlagService;

    @Nested
    class GetWahlvorschlaegeForWahlAndWahlbezirk {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GETWAHLVORSCHLAEGE);

            Assertions.assertThatException().isThrownBy(() -> wahlvorschlagService.getWahlvorschlaegeForWahlAndWahlbezirk("wahlID", "wahlbezirkID"))
                .isInstanceOf(
                    NoSearchResultFoundException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> wahlvorschlagService.getWahlvorschlaegeForWahlAndWahlbezirk("wahlID", "wahlbezirkID"))
                .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GETWAHLVORSTANDFORWAHLBEZIRK);
        }
    }

    @Nested
    class GetWahlvorschlaegeListeForWahltagAndWahlID {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GETWAHLVORSCHLAEGELISTE);

            Assertions.assertThatException()
                .isThrownBy(() -> wahlvorschlagService.getWahlvorschlaegeListeForWahltagAndWahlID(LocalDate.of(2024, 10, 10), "wahlID"))
                .isInstanceOf(
                    NoSearchResultFoundException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> wahlvorschlagService.getWahlvorschlaegeListeForWahltagAndWahlID(LocalDate.of(2024, 10, 10), "wahlID"))
                .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GETWAHLVORSCHLAEGELISTE);
        }
    }

    @Nested
    class GetReferendumvorlagenForWahlAndWahlbezirk {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GETREFERENDUMVORLAGEN);

            Assertions.assertThatException()
                .isThrownBy(() -> wahlvorschlagService.getReferendumvorlagenForWahlAndWahlbezirk("wahlID", "wahlbezirkID"))
                .isInstanceOf(
                    NoSearchResultFoundException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> wahlvorschlagService.getReferendumvorlagenForWahlAndWahlbezirk("wahlID", "wahlbezirkID"))
                .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GETREFERENDUMVORLAGEN);
        }
    }

}
