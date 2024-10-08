package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
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
class UngueltigeWahlscheineServiceTest {

    @Mock
    UngueltigeWahlscheineModelMapper ungueltigeWahlscheineModelMapper;

    @Mock
    UngueltigeWahlscheineValidator ungueltigeWahlscheineValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    UngueltigeWahlscheineRepository ungueltigeWahlscheineRepository;

    @InjectMocks
    UngueltigeWahlscheineService unitUnderTest;

    @Nested
    class GetUngueltigeWahlscheine {

        @Test
        void dataFound() {
            val ungueltigeWahlscheineReferenceModel = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            val mockedEntityUngueltigeWahlscheine = "csv-data".getBytes();
            val mockedEntity = new UngueltigeWahlscheine();
            mockedEntity.setUngueltigeWahlscheine(mockedEntityUngueltigeWahlscheine);
            val mockedMappedEntityID = new WahltagIdUndWahlbezirksart();

            Mockito.when(ungueltigeWahlscheineModelMapper.toID(ungueltigeWahlscheineReferenceModel)).thenReturn(mockedMappedEntityID);
            Mockito.when(ungueltigeWahlscheineRepository.findById(mockedMappedEntityID)).thenReturn(Optional.of(mockedEntity));

            val result = unitUnderTest.getUngueltigeWahlscheine(ungueltigeWahlscheineReferenceModel);

            Assertions.assertThat(result).isEqualTo(mockedEntityUngueltigeWahlscheine);
            Assertions.assertThat(result).isNotSameAs(mockedEntityUngueltigeWahlscheine);
            Mockito.verify(ungueltigeWahlscheineValidator).validUngueltigeWahlscheineReferenceOrThrow(ungueltigeWahlscheineReferenceModel);
        }

        @Test
        void exceptionWhenNoDataFound() {
            val ungueltigeWahlscheineReferenceModel = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            val mockedMappedEntityID = new WahltagIdUndWahlbezirksart();
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(ungueltigeWahlscheineModelMapper.toID(ungueltigeWahlscheineReferenceModel)).thenReturn(mockedMappedEntityID);
            Mockito.when(ungueltigeWahlscheineRepository.findById(mockedMappedEntityID)).thenReturn(Optional.empty());
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getUngueltigeWahlscheine(ungueltigeWahlscheineReferenceModel)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class SetUngueltigeWahlscheine {

        @Test
        void dataSet() {
            val ungueltigeWahlscheineData = "csv data to set".getBytes();
            val ungueltigeWahlscheineWriteModel = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel("wahlID", WahlbezirkArtModel.BWB),
                    ungueltigeWahlscheineData);

            val mockedMappedEntity = new UngueltigeWahlscheine();

            Mockito.when(ungueltigeWahlscheineModelMapper.toEntity(ungueltigeWahlscheineWriteModel)).thenReturn(mockedMappedEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(ungueltigeWahlscheineWriteModel));

            Mockito.verify(ungueltigeWahlscheineValidator).validUngueltigeWahlscheineWriteModelOrThrow(ungueltigeWahlscheineWriteModel);
            Mockito.verify(ungueltigeWahlscheineRepository).save(mockedMappedEntity);
        }

        @Test
        void wlsExceptionWhenSavingFailed() {
            val ungueltigeWahlscheineData = "csv data to set".getBytes();
            val ungueltigeWahlscheineWriteModel = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel("wahlID", WahlbezirkArtModel.BWB),
                    ungueltigeWahlscheineData);

            val mockedMappedEntity = new UngueltigeWahlscheine();
            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(ungueltigeWahlscheineModelMapper.toEntity(ungueltigeWahlscheineWriteModel)).thenReturn(mockedMappedEntity);
            Mockito.doThrow(mockedRepoSaveException).when(ungueltigeWahlscheineRepository).save(mockedMappedEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTUNGUELTIGEWS_SPEICHERN_NICHT_ERFOLGREICH))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(ungueltigeWahlscheineWriteModel)).isSameAs(mockedWlsException);
        }
    }

}
