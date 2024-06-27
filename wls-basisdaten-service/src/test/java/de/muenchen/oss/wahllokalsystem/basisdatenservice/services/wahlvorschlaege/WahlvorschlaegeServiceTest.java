package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlvorschlaegeServiceTest {

    @Mock
    WahlvorschlaegeRepository wahlvorschlaegeRepository;
    @Mock
    WahlvorschlagRepository wahlvorschlagRepository;
    @Mock
    KandidatRepository kandidatRepository;
    @Mock
    WahlvorschlaegeModelMapper wahlvorschlaegeModelMapper;
    @Mock
    WahlvorschlaegeValidator wahlvorschlaegeValidator;
    @Mock
    WahlvorschlaegeClient wahlvorschlaegeClient;
    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorschlaegeService unitUnderTest;

    @Nested
    class GetWahlvorschlaege {

        @Test
        void missingDataIsLoadedAndStored() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedClientResponse = WahlvorschlaegeModel.builder().build();
            val mockedWahlvorschlagEntity1 = new Wahlvorschlag();

            val mockedKandidatEntity1 = new Kandidat();
            mockedKandidatEntity1.setId(UUID.randomUUID());
            val mockedKandidatEntity2 = new Kandidat();
            mockedKandidatEntity2.setId(UUID.randomUUID());
            mockedWahlvorschlagEntity1.setKandidaten(Set.of(mockedKandidatEntity1, mockedKandidatEntity2));
            val mockedWahlvorschlagEntity2 = new Wahlvorschlag();

            val mockedKandidatEntity3 = new Kandidat();
            mockedKandidatEntity3.setId(UUID.randomUUID());
            val mockedKandidatEntity4 = new Kandidat();
            mockedKandidatEntity4.setId(UUID.randomUUID());
            mockedWahlvorschlagEntity2.setKandidaten(Set.of(mockedKandidatEntity3, mockedKandidatEntity4));
            val mockedMappedEntity = new Wahlvorschlaege();

            mockedMappedEntity.setWahlvorschlaege(Set.of(mockedWahlvorschlagEntity1, mockedWahlvorschlagEntity2));

            val mockedMappedSavedEntity = WahlvorschlaegeModel.builder().build();

            Mockito.when(wahlvorschlaegeRepository.findByBezirkUndWahlID(wahlUndBezirkID)).thenReturn(Optional.empty());
            Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(wahlUndBezirkID)).thenReturn(mockedClientResponse);
            Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedClientResponse)).thenReturn(mockedMappedEntity);
            Mockito.when(wahlvorschlaegeModelMapper.toModel(mockedMappedEntity)).thenReturn(mockedMappedSavedEntity);

            val result = unitUnderTest.getWahlvorschlaege(wahlUndBezirkID);

            Assertions.assertThat(result).isSameAs(mockedMappedSavedEntity);
            Mockito.verify(wahlvorschlaegeRepository).save(mockedMappedEntity);
            Mockito.verify(wahlvorschlagRepository).save(mockedWahlvorschlagEntity1);
            Mockito.verify(wahlvorschlagRepository).save(mockedWahlvorschlagEntity2);
            Mockito.verify(kandidatRepository).saveAll(mockedWahlvorschlagEntity1.getKandidaten());
            Mockito.verify(kandidatRepository).saveAll(mockedWahlvorschlagEntity2.getKandidaten());
        }

        @Test
        void existingDataIsLoaded() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedWahlvorschlaegeFromRepo = new Wahlvorschlaege();
            val mockedMappedWahlvorschlaegeAsModel = WahlvorschlaegeModel.builder().build();

            Mockito.when(wahlvorschlaegeRepository.findByBezirkUndWahlID(wahlUndBezirkID)).thenReturn(Optional.of(mockedWahlvorschlaegeFromRepo));
            Mockito.when(wahlvorschlaegeModelMapper.toModel(mockedWahlvorschlaegeFromRepo)).thenReturn(mockedMappedWahlvorschlaegeAsModel);

            val result = unitUnderTest.getWahlvorschlaege(wahlUndBezirkID);

            Assertions.assertThat(result).isSameAs(mockedMappedWahlvorschlaegeAsModel);
            Mockito.verify(wahlvorschlaegeClient, times(0)).getWahlvorschlaege(any());
        }

        @Test
        void validationExceptionGotThrown() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlvorschlaegeValidator).validWahlIdUndWahlbezirkIDOrThrow(wahlUndBezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(wahlUndBezirkID)).isSameAs(mockedValidationException);
        }

        @Test
        void clientExceptionGotThrown() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedClientException = new RuntimeException("client failed");

            Mockito.doThrow(mockedClientException).when(wahlvorschlaegeClient).getWahlvorschlaege(wahlUndBezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(wahlUndBezirkID)).isSameAs(mockedClientException);
        }

        @Nested
        class PersistingExceptionProducesWlsException {

            @Test
            void onExceptionOfWahlvorschlaegeRepository() {
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
                val mockedWahlvorschlaegeModel = WahlvorschlaegeModel.builder().build();
                val mockedWahlvorschlaegeEntity = new Wahlvorschlaege();
                val mockedPersistingException = new RuntimeException("persisting failed");

                Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(wahlUndBezirkID)).thenReturn(mockedWahlvorschlaegeModel);
                Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedWahlvorschlaegeModel)).thenReturn(mockedWahlvorschlaegeEntity);
                Mockito.doThrow(mockedPersistingException).when(wahlvorschlaegeRepository).save(mockedWahlvorschlaegeEntity);
                Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedWlsException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(wahlUndBezirkID)).isSameAs(mockedWlsException);
            }

            @Test
            void onExceptionOfWahlvorschlagRepository() {
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
                val mockedWahlvorschlaegeModel = WahlvorschlaegeModel.builder().build();
                val mockedWahlvorschlaegeEntity = new Wahlvorschlaege();
                mockedWahlvorschlaegeEntity.setWahlvorschlaege(Set.of(new Wahlvorschlag()));
                val mockedPersistingException = new RuntimeException("persisting failed");

                Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(wahlUndBezirkID)).thenReturn(mockedWahlvorschlaegeModel);
                Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedWahlvorschlaegeModel)).thenReturn(mockedWahlvorschlaegeEntity);
                Mockito.doThrow(mockedPersistingException).when(wahlvorschlagRepository).save(any());
                Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedWlsException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(wahlUndBezirkID)).isSameAs(mockedWlsException);
            }

            @Test
            void onExceptionOfKandidatRepository() {
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val wahlUndBezirkID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
                val mockedWahlvorschlaegeModel = WahlvorschlaegeModel.builder().build();
                val mockedWahlvorschlaegeEntity = new Wahlvorschlaege();
                val mockedWahlvorschlagEntity = new Wahlvorschlag();
                mockedWahlvorschlagEntity.setKandidaten(Set.of(new Kandidat()));
                mockedWahlvorschlaegeEntity.setWahlvorschlaege(Set.of(mockedWahlvorschlagEntity));
                val mockedPersistingException = new RuntimeException("persisting failed");

                Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(wahlUndBezirkID)).thenReturn(mockedWahlvorschlaegeModel);
                Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedWahlvorschlaegeModel)).thenReturn(mockedWahlvorschlaegeEntity);
                Mockito.doThrow(mockedPersistingException).when(kandidatRepository).saveAll(any());
                Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedWlsException);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(wahlUndBezirkID)).isSameAs(mockedWlsException);
            }
        }

    }

}
