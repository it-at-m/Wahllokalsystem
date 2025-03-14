package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Waehlerverzeichnis;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.WaehlerverzeichnisRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
class WaehlerverzeichnisServiceTest {

    @Mock
    WaehlerverzeichnisRepository waehlerverzeichnisRepository;

    @Mock
    WaehlerverzeichnisValidator waehlerverzeichnisValidator;

    @Mock
    WaehlerverzeichnisModelMapper waehlerverzeichnisModelMapper;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WaehlerverzeichnisService unitUnderTest;

    @Nested
    class GetWaehlerverzeichnis {

        @Test
        void should_returnWaehlerverzeichnis_when_givenValidReference() {
            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 123L);

            val mockedRepoResponse = new Waehlerverzeichnis();
            val mockedResponseAsModel = WaehlerverzeichnisModel.builder().build();

            Mockito.when(waehlerverzeichnisRepository.findById(waehlerverzeichnisReference)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(waehlerverzeichnisModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedResponseAsModel);

            val result = unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference);

            Assertions.assertThat(result.get()).isSameAs(mockedResponseAsModel);
        }

        @Test
        void should_returnEmpty_when_noDataFound() {
            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 123L);

            Mockito.when(waehlerverzeichnisRepository.findById(waehlerverzeichnisReference)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void should_throwException_when_validationFailed() {
            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 123L);

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(waehlerverzeichnisValidator).validWaehlerverzeichnisReferenceOrThrow(waehlerverzeichnisReference);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference))
                    .isSameAs(mockedValidationException);
        }
    }

    @Nested
    class SetWaehlverzeichnis {

        @Test
        void should_saveWaehlerverzeichnis_when_givenValidModel() {
            val modelToSave = WaehlerverzeichnisModel.builder().build();

            val mockedMappedEntity = new Waehlerverzeichnis();

            Mockito.when(waehlerverzeichnisModelMapper.toEntity(modelToSave)).thenReturn(mockedMappedEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSave));

            Mockito.verify(waehlerverzeichnisRepository).save(mockedMappedEntity);
        }

        @Test
        void should_notSaveWaehlerverzeichnis_when_validationFailed() {
            val modelToSave = WaehlerverzeichnisModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(waehlerverzeichnisValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwTechnischeWlsException_when_savingFailed() {
            val modelToSave = WaehlerverzeichnisModel.builder().build();

            val mockedMappedEntity = new Waehlerverzeichnis();
            val mockedSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(waehlerverzeichnisModelMapper.toEntity(modelToSave)).thenReturn(mockedMappedEntity);
            Mockito.doThrow(mockedSaveException).when(waehlerverzeichnisRepository).save(mockedMappedEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSave)).isSameAs(mockedWlsException);
        }
    }
}
