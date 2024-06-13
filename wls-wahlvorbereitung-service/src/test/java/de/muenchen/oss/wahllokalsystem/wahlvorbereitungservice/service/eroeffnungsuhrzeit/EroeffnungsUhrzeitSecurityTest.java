package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import java.time.LocalDateTime;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class EroeffnungsUhrzeitSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    EroeffnungsUhrzeitService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetEroeffnungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_EROEFFNUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_EROEFFNUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_EROEFFNUNGSUHRZEIT);
        }
    }

    @Nested
    class SetEroeffnungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new EroeffnungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new EroeffnungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedOnServiceAuthorityMissing() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_REPO_EROEFFNUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new EroeffnungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnRepoWriteAuthorityMissing() {

            String[] ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT_WHITHOUT_REPO_WRITE = Arrays.stream(Authorities.ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT)
                    .filter(auth -> !auth.equals(Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT))
                    .toArray(String[]::new);

            SecurityUtils.runAs(ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT_WHITHOUT_REPO_WRITE);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new EroeffnungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSet))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }

    }
}
