package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

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
public class FortsetzungsUhrzeitSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    FortsetzungsUhrzeitService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetFortsetzungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getFortsetzungsUhrzeit(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getFortsetzungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getFortsetzungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_FORTSETZUNGSUHRZEIT);
        }
    }

    @Nested
    class SetFortsetzungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new FortsetzungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setFortsetzungsUhrzeit(modelToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new FortsetzungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setFortsetzungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedOnServiceAuthorityMissing() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_REPO_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new FortsetzungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setFortsetzungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnRepoWriteAuthorityMissing() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_FORTSETZUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new FortsetzungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setFortsetzungsUhrzeit(modelToSet))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }

    }
}
