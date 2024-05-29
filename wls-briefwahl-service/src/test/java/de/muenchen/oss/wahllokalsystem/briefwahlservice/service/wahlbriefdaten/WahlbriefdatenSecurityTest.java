package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
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
public class WahlbriefdatenSecurityTest {

    private final String TESTUSER = "testuser";
    private final String TESTUSER_PASSWORD = "secret";

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    WahlbriefdatenService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetWahlbriefdaten {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_GET_WAHLBRIEFDATEN);

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWahlbriefdaten(wahlbezirkID));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_GET_WAHLBRIEFDATEN);

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWahlbriefdaten(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWahlbriefdaten(wahlbezirkID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            val requiredAuthorities = Authorities.ALL_AUTHORITIES_GET_WAHLBRIEFDATEN;
            return Arrays.stream(requiredAuthorities)
                    .map(authorityToRemove ->
                    //remove one authority from all required authorities
                    Arguments.of(Arrays.stream(requiredAuthorities)
                            .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
        }

    }

    @Nested
    class SetWahlbriefdaten {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_SET_WAHLBRIEFDATEN);

            val wahlbezirkID = "wahlbezirkID";
            val wahlbriefdatenToSet = new WahlbriefdatenModel(wahlbezirkID, null, null, null, null, null);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setWahlbriefdaten(wahlbriefdatenToSet));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, Authorities.ALL_AUTHORITIES_SET_WAHLBRIEFDATEN);

            val wahlbezirkID = "wahlbezirkID";
            val wahlbriefdatenToSet = new WahlbriefdatenModel(wahlbezirkID, null, null, null, null, null);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setWahlbriefdaten(wahlbriefdatenToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(TESTUSER, TESTUSER_PASSWORD, argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            val wahlbriefdatenToSet = new WahlbriefdatenModel(wahlbezirkID, null, null, null, null, null);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setWahlbriefdaten(wahlbriefdatenToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            val requiredAuthorities = Authorities.ALL_AUTHORITIES_SET_WAHLBRIEFDATEN;
            return Arrays.stream(requiredAuthorities)
                    .map(authorityToRemove ->
                    //remove one authority from all required authorities
                    Arguments.of(Arrays.stream(requiredAuthorities)
                            .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
        }
    }
}
