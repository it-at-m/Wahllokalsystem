package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
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
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
public class EreignisServiceSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    EreignisService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetEreignis {

        @Test
        void should_notThrowException_when_givenAllAuthorities() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getEreignisse(wahlbezirkID));
        }

        @Test
        void should_throwAccessDeniedException_when_bezirkIDPermissionEvaluatorReturnsFalse() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEreignisse(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyAuthorityMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEreignisse(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
        }
    }

    @Nested
    class SetEreignis {

        @Test
        void should_notThrowException_when_givenAllAuthorities() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel));
        }

        @Test
        void should_throwAccessDeniedException_when_bezirkIDPermissionEvaluatorReturnsFalse() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} {1} missing")
        @MethodSource("getMissingServiceAuthoritiesVariations")
        void should_throwAccessDeniedException_whenServiceAuthoritiesMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_REPO_AUTHORITIES_SET_EREIGNISSE, argumentsAccessor.get(0, String[].class)));
            val wahlbezirkID = "wahlbezirkID";
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingServiceAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE);
        }

        @ParameterizedTest(name = "{index} {1} missing")
        @MethodSource("getMissingRepositoryAuthoritiesVariations")
        void should_throwTechnischeWlsException_when_repositoryAuthoritiesMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE, argumentsAccessor.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignisse(mockedEreignisseWriteModel)).isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingRepositoryAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_REPO_AUTHORITIES_SET_EREIGNISSE);
        }
    }
}
