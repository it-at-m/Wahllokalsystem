package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung.BegruendungDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class BegruendungServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    BegruendungService unitUnderTest;

    @Autowired
    BegruendungRepository begruendungRepository;

    @Autowired
    BegruendungDTOMapper begruendungDTOMapper;

    @AfterEach
    void teardown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEGRUENDUNG);
        begruendungRepository.deleteAll();
    }

    @Nested
    class GetBegruendung {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_BEGRUENDUNG);

            val begruendungReference = begruendungDTOMapper.toReferenceModel("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(begruendungReference.wahlbezirkID()), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getBegruendung(begruendungReference));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getBegruendung(begruendungDTOMapper.toReferenceModel("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A)))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_BEGRUENDUNG);
        }
    }

    @Nested
    class SetBegruendung {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG);
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newBegruendung = new BegruendungModel(wahlbezirkID, wahlID, stapelartModel, "grund1", "grund2", true, true);
            val newBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postBegruendung(newBegruendungReference, newBegruendung));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG);
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newBegruendung = new BegruendungModel(wahlbezirkID, wahlID, stapelartModel, "grund1", "grund2", true, true);
            val newBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postBegruendung(newBegruendungReference, newBegruendung))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingAccessDenied")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_ACCESS_DENIED, arguments.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newBegruendung = new BegruendungModel(wahlbezirkID, wahlID, stapelartModel, "grund1", "grund2", true, true);
            val newBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postBegruendung(newBegruendungReference, newBegruendung))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingTechnischeWlsException")
        void should_throwTechnischeWlsException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_ACCESS_DENIED, arguments.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newBegruendung = new BegruendungModel(wahlbezirkID, wahlID, stapelartModel, "grund1", "grund2", true, true);
            val newBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postBegruendung(newBegruendungReference, newBegruendung))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingAccessDenied() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_ACCESS_DENIED);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingTechnischeWlsException() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);
        }
    }
}
