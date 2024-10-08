package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch.HandbuchRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = MicroServiceApplication.class)
public class HandbuchServiceSecurityTest {

    @Autowired
    HandbuchService handbuchService;

    @Autowired
    HandbuchRepository handbuchRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_HANDBUCH);
        handbuchRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetHandbuch {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_HANDBUCH);
            handbuchRepository.save(new Handbuch(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), "handbuch".getBytes()));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_HANDBUCH);

            val handbuchID = new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB);
            Assertions.assertThatNoException().isThrownBy(() -> handbuchService.getHandbuch(handbuchID));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val handbuchID = new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB);
            Assertions.assertThatThrownBy(() -> handbuchService.getHandbuch(handbuchID)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_HANDBUCH);
        }

    }

    @Nested
    class SetHandbuch {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_HANDBUCH);

            val handbuchModelToSave = new HandbuchWriteModel(new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB), "handbuch".getBytes());
            Assertions.assertThatNoException().isThrownBy(() -> handbuchService.setHandbuch(handbuchModelToSave));
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_HANDBUCH);

            val handbuchModelToSave = new HandbuchWriteModel(new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB), "handbuch".getBytes());
            Assertions.assertThatThrownBy(() -> handbuchService.setHandbuch(handbuchModelToSave)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void technischeWlsExceptionWhenRepoAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_HANDBUCH);

            val handbuchModelToSave = new HandbuchWriteModel(new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB), "handbuch".getBytes());
            Assertions.assertThatThrownBy(() -> handbuchService.setHandbuch(handbuchModelToSave)).isInstanceOf(TechnischeWlsException.class);
        }
    }
}
