package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
public class EreignisServiceSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    EreignisService unitUnderTest;

    @Autowired
    EreignisRepository ereignisRepository;

    @BeforeEach
    void setup() { SecurityContextHolder.clearContext(); }

    @Nested
    class GetEreignis {

        @Test
        void accessGranted() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getEreignis(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEreignis(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEreignis(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
        }
    }

    @Nested
    class SetEreignis {

        @Test
        void accessGranted() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(ereignisModel));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.ALL_REPO_AUTHORITIES_SET_EREIGNISSE);

            val ereignisModel = TestdataFactory.createEreignisModelWithData("wahlbezirkID", "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void technischeWlsExceptionWhenRepoAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE);

            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisModel)).isInstanceOf(TechnischeWlsException.class);

        }
    }
}
