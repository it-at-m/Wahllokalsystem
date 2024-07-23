package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class UngueltigeWahlscheineServiceSecurityTest {

    @Autowired
    UngueltigeWahlscheineService unitUnderTest;

    @Autowired
    UngueltigeWahlscheineRepository ungueltigeWahlscheineRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_UNGUELTIGEWAHLSCHEINE);
        ungueltigeWahlscheineRepository.deleteAll();
    }

    @Nested
    class GetUngueltigeWahlscheine {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE);
            val ungueltigeWahlscheineToGet = new UngueltigeWahlscheine(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), "data".getBytes());
            ungueltigeWahlscheineRepository.save(ungueltigeWahlscheineToGet);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE);

            val ungueltigeWahlscheineReferenceModel = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getUngueltigeWahlscheine(ungueltigeWahlscheineReferenceModel));

        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE);
            val ungueltigeWahlscheineToGet = new UngueltigeWahlscheine(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), "data".getBytes());
            ungueltigeWahlscheineRepository.save(ungueltigeWahlscheineToGet);

            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
            val ungueltigeWahlscheineReferenceModel = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.UWB);
            Assertions.assertThatThrownBy(() -> unitUnderTest.getUngueltigeWahlscheine(ungueltigeWahlscheineReferenceModel))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE);
        }
    }

    @Nested
    class SetUngueltigeWahlscheine {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE);
            val writeModel = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.BWB),
                    "data".getBytes());
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(writeModel));
        }

        @Test
        void accessDeniedWhenServiceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE);
            val writeModel = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.BWB),
                    "data".getBytes());
            Assertions.assertThatThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(writeModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void technischeWlsExceptionWhenRepoAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_UNGUELTIGEWAHLSCHEINE);
            val writeModel = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.BWB),
                    "data".getBytes());
            Assertions.assertThatThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(writeModel)).isInstanceOf(TechnischeWlsException.class);
        }
    }
}
