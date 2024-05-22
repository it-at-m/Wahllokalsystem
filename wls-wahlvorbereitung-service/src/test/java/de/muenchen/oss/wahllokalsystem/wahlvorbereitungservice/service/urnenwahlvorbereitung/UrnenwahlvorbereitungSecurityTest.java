package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import java.util.List;
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
public class UrnenwahlvorbereitungSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    UrnenwahlvorbereitungService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetUrnenwahlvorbereitung {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_URNENWAHLVORBEREITUNG);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_GET_URNENWAHLVORBEREITUNG);

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_URNENWAHLVORBEREITUNG);
        }
    }

    @Nested
    class SetUrnenwahlvorbereitung {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_URNENWAHLVORBEITUNG);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlvorbereitungModel(wahlbezirkID, 0, 0, 0, List.of(new WahlurneModel("wahlID", 1, true)));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_URNENWAHLVORBEITUNG);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlvorbereitungModel(wahlbezirkID, 0, 0, 0, List.of(new WahlurneModel("wahlID", 1, true)));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSet))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        @Test
        void accessDeniedOnServiceAuthorityMissing() {
            SecurityUtils.runAs(Authorities.REPOSITORY_WRITE_URNENWAHLVORBEREITUNG);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlvorbereitungModel(wahlbezirkID, 0, 0, 0, List.of(new WahlurneModel("wahlID", 1, true)));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSet))
                    .isExactlyInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnRepoWriteAuthorityMissing() {
            SecurityUtils.runAs(Authorities.SERVICE_POST_URNENWAHLVORBEREITUNG);

            val wahlbezirkID = "wahlbezirkID";
            val modelToSet = new UrnenwahlvorbereitungModel(wahlbezirkID, 0, 0, 0, List.of(new WahlurneModel("wahlID", 1, true)));

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSet))
                    .isExactlyInstanceOf(TechnischeWlsException.class);
        }
    }
}
