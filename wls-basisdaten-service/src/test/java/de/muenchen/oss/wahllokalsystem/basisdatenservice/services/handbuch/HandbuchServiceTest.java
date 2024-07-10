package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.HandbuchRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
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
class HandbuchServiceTest {

    @Mock
    HandbuchValidator handbuchValidator;

    @Mock
    HandbuchModelMapper handbuchModelMapper;

    @Mock
    HandbuchRepository handbuchRepository;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    HandbuchService unitUnderTest;

    @Nested
    class GetHandbuch {

        @Test
        void dataFound() {
            val handbuchReference = new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            val mockedHandbuchID = new WahltagIdUndWahlbezirksart();
            val mockedRepoResponse = new Handbuch(null, "handbuch.txt".getBytes());

            Mockito.when(handbuchModelMapper.toEntityID(handbuchReference)).thenReturn(mockedHandbuchID);
            Mockito.when(handbuchRepository.findById(mockedHandbuchID)).thenReturn(Optional.of(mockedRepoResponse));

            val result = unitUnderTest.getHandbuch(handbuchReference);

            Assertions.assertThat(result).isEqualTo("handbuch.txt".getBytes());
        }

        @Test
        void noDataFound() {
            val handbuchReference = new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            val mockedHandbuchID = new WahltagIdUndWahlbezirksart();
            val mockedException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(handbuchModelMapper.toEntityID(handbuchReference)).thenReturn(mockedHandbuchID);
            Mockito.when(handbuchRepository.findById(mockedHandbuchID)).thenReturn(Optional.empty());
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETHANDBUCH_KEINE_DATEN)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getHandbuch(handbuchReference)).isSameAs(mockedException);
        }
    }

    @Nested
    class SetHandbuch {

        @Test
        void dataSuccessfullySaved() {
            val handbuchToSave = HandbuchWriteModel.builder().build();

            val mockedModelMappedToEntity = new Handbuch();

            Mockito.when(handbuchModelMapper.toEntity(handbuchToSave)).thenReturn(mockedModelMappedToEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setHandbuch(handbuchToSave));

            Mockito.verify(handbuchRepository).save(mockedModelMappedToEntity);
        }

        @Test
        void noSaveOnValidationError() {
            val handbuchToSave = HandbuchWriteModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(handbuchValidator).validHandbuchWriteModelOrThrow(handbuchToSave);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setHandbuch(handbuchToSave)).isSameAs(mockedValidationException);

            Mockito.verify(handbuchRepository, times(0)).save(Mockito.any());
        }

        @Test
        void onSaveExceptionIsMappedToWlsException() {
            val handbuchToSave = HandbuchWriteModel.builder().build();

            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
            val mockedOnSaveException = new RuntimeException("saving failed");
            val mockedModelMappedToEntity = new Handbuch();

            Mockito.when(handbuchModelMapper.toEntity(handbuchToSave)).thenReturn(mockedModelMappedToEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH))
                    .thenReturn(mockedWlsException);
            Mockito.doThrow(mockedOnSaveException).when(handbuchRepository).save(mockedModelMappedToEntity);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setHandbuch(handbuchToSave)).isSameAs(mockedWlsException);
        }
    }

}
