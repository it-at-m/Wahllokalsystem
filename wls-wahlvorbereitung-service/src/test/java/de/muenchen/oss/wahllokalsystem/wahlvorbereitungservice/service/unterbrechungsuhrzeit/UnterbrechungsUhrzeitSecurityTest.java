package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
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
public class UnterbrechungsUhrzeitSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    UnterbrechungsUhrzeitService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetUnterbrechungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_UNTERBRECHUNGSUHRZEIT);
        }
    }

    @Nested
    class SetUnterbrechungsUhrzeit {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UnterbrechungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UnterbrechungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedOnServiceAuthorityMissing() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_REPO_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UnterbrechungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnRepoWriteAuthorityMissing() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_UNTERBRECHUNGSUHRZEIT);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UnterbrechungsUhrzeitModel(wahlbezirkID, LocalDateTime.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSet))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }

    }
}
