package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class UrnenwahlSchliessungsUhrzeitSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    UrnenwahlSchliessungsUhrzeitService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetUrnenwahlSchliessungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_URNENWAHLSCHLIESSUNGSUHRZEIT);
        }
    }

    @Nested
    class SetUrnenwahlSchliessungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedOnServiceAuthorityMissing() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_REPO_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnRepoWriteAuthorityMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_URNENWAHLSCHLIESSUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSet))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }

    }
}
