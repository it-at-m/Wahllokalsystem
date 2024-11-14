package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles.DUMMY_CLIENTS;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
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
@ActiveProfiles({ SPRING_TEST_PROFILE, DUMMY_CLIENTS })
public class WahlvorstandServiceSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    WahlvorstandService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetWahlvorstand {

        @Test
        void should_notThrowException_when_givenAllAuthorities() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLVORSTAND);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWahlvorstand(wahlbezirkID));
        }

        @Test
        void should_throwAccessDeniedException_when_bezirkIDPermissionEvaluatorReturnsFalse() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLVORSTAND);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWahlvorstand(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyAuthorityMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWahlvorstand(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_WAHLVORSTAND);
        }
    }

    @Nested
    class PostWahlvorstand {

        @Test
        void should_notThrowException_when_givenAllAuthorities() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_WAHLVORSTAND);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlvorstand(mockedWahlvorstandModel));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingServiceAuthoritiesVariations")
        void should_throwAccessDeniedException_when_serviceAuthoritiesMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_REPO_AUTHORITIES_POST_WAHLVORSTAND, argumentsAccessor.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);
            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlvorstand(mockedWahlvorstandModel)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingServiceAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_SERVICE_AUTHORITIES_POST_WAHLVORSTAND);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingRepositoryAuthoritiesVariations")
        void should_throwTechnischeWlsException_when_repositoryAuthoritiesMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_SERVICE_AUTHORITIES_POST_WAHLVORSTAND, argumentsAccessor.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);
            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlvorstand(mockedWahlvorstandModel)).isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingRepositoryAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_REPO_AUTHORITIES_POST_WAHLVORSTAND);
        }
    }
}
