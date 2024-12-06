package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import wiremock.org.apache.commons.lang3.ArrayUtils;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class StatusServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    StatusService unitUnderTest;

    @Autowired
    StatusRepository statusRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STATUS);
        statusRepository.deleteAll();
    }

    @Nested
    class GetStatus {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_STATUS);

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getStatus(id));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getStatus(id)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_STATUS);
        }
    }

    @Nested
    class SetStatus {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STATUS);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val newStatus = new StatusModel(id, new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setStatus(id, newStatus));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STATUS);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val newStatus = new StatusModel(id, new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStatus(id, newStatus)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingAccessDenied")
        void should_throwAccessDeniedException_when_anyRequiredServiceAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_WLS_EXCEPTION, arguments.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val newStatus = new StatusModel(id, new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStatus(id, newStatus)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThrowingTechnischeWlsException")
        void should_throwTechnischeWlsException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_ACCESS_DENIED, arguments.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val newStatus = new StatusModel(id, new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStatus(id, newStatus)).isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingAccessDenied() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_ACCESS_DENIED);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThrowingTechnischeWlsException() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);
        }
    }

}
