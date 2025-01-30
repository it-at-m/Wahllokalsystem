package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.ErgebnisseDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.ArrayList;
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
class ErgebnisseServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    ErgebnisseService unitUnderTest;

    @Autowired
    ErgebnisseRepository ergebnisseRepository;

    @Autowired
    ErgebnisseDTOMapper ergebnisseDTOMapper;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_ERGEBNISSE);
        ergebnisseRepository.deleteAll();
    }

    @Nested
    class GetErgebnisse {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_ERGEBNISSE);

            val ergebnisseReference = ergebnisseDTOMapper.toReferenceModel("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(ergebnisseReference.wahlbezirkID()), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getErgebnisse(ergebnisseReference));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getErgebnisse(ergebnisseDTOMapper.toReferenceModel("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A)))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_ERGEBNISSE);
        }
    }

    @Nested
    class GetAllErgebnisse {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_ERGEBNISSE);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getAllErgebnisse(wahlID, wahlbezirkID));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIdDoesNotMatch() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_ERGEBNISSE);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getAllErgebnisse(wahlID, wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getAllErgebnisse(wahlID, wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_ALL_ERGEBNISSE);
        }

    }

    @Nested
    class PostErgebnisse {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE);
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val ergebnisModel1 = new ErgebnisModel(null, null, null, 1, null);
            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            newErgebnisModelList.add(ergebnisModel1);

            val newErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, newErgebnisModelList);
            val newErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postErgebnisse(newErgebnisseReference, newErgebnisse));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE);
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            val newErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, newErgebnisModelList);
            val newErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postErgebnisse(newErgebnisseReference, newErgebnisse))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingAccessDenied")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_ACCESS_DENIED, arguments.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            val newErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, newErgebnisModelList);
            val newErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postErgebnisse(newErgebnisseReference, newErgebnisse))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingTechnischeWlsException")
        void should_throwTechnischeWlsException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_ACCESS_DENIED, arguments.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;

            val ergebnisModel1 = new ErgebnisModel(null, null, null, 1, null);
            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            newErgebnisModelList.add(ergebnisModel1);

            val newErgebnisse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, newErgebnisModelList);
            val newErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postErgebnisse(newErgebnisseReference, newErgebnisse))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingAccessDenied() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_ACCESS_DENIED);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingTechnischeWlsException() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);
        }
    }
}
