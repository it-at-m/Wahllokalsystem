package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.ArrayList;
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
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
public class EreignisServiceSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    EreignisService unitUnderTest;

    @Autowired
    EreignisRepository ereignisRepository;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetEreignis {

        @Test
        void should_not_throw_exception_when_given_all_authorities() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getEreignis(wahlbezirkID));
        }

        @Test
        void should_throw_AccessDeniedException_when_bezirkIDPermissionEvaluator_missing() {
            val wahlbezirkID = "wahlbezirkID";

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getEreignis(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throw_AccessDeniedException_when_any_authority_missing(final ArgumentsAccessor argumentsAccessor) {
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
        void should_not_throw_exception_when_given_all_authorities() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData());
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel));
        }

        @Test
        void should_throw_AccessDeniedException_when_bezirkIDPermissionEvaluator_missing() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData());
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_throw_AccessDeniedException_when_service_authorities_missing() {
            SecurityUtils.runWith(Authorities.ALL_REPO_AUTHORITIES_SET_EREIGNISSE);
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData());
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_throw_TechnischeWlsException_when_repository_authorities_missing() {
            SecurityUtils.runWith(Authorities.ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE);

            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData());
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel)).isInstanceOf(TechnischeWlsException.class);
        }
    }
}
