package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
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
class EroeffnungsUhrzeitServiceTest {

    @Mock
    EroeffnungsUhrzeitRepository eroeffnungsUhrzeitRepository;

    @Mock
    EroeffnungsUhrzeitModelMapper eroeffnungsUhrzeitModelMapper;

    @Mock
    EroeffnungsUhrzeitValidator eroeffnungsUhrzeitValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    EroeffnungsUhrzeitService unitUnderTest;

    @Nested
    class getEroeffnungsUhrzeit {

        @Test
        void should_returnEroeffnungsuhrzeit_when_givenValidWahlbezirkID() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new EroeffnungsUhrzeit();
            val mockedMappedRepoResponseAsModel = EroeffnungsUhrzeitModel.builder().build();

            Mockito.doNothing().when(eroeffnungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(eroeffnungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(eroeffnungsUhrzeitModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void should_returnEmpty_when_noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(eroeffnungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(eroeffnungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(eroeffnungsUhrzeitModelMapper, times(0)).toModel(any());
        }

        @Test
        void should_throwException_when_validationFailed() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(eroeffnungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getEroeffnungsUhrzeit(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

    @Nested
    class SetEroeffnungsUhrzeit {

        @Test
        void should_saveEroeffnungsuhrzeit_when_givenValidModel() {
            val modelToSave = EroeffnungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new EroeffnungsUhrzeit();
            Mockito.when(eroeffnungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);

            Mockito.doNothing().when(eroeffnungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            unitUnderTest.setEroeffnungsUhrzeit(modelToSave);

            Mockito.verify(eroeffnungsUhrzeitRepository).save(mockedModelAsEntity);
        }

        @Test
        void should_notSaveEroeffnungsuhrzeit_when_validationFailed() {
            val modelToSave = EroeffnungsUhrzeitModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(eroeffnungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwTechnischeWlsException_when_savingFailed() {
            val modelToSave = EroeffnungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new EroeffnungsUhrzeit();
            val mockedSaveException = new RuntimeException("fail on save");
            val mockedFactoryException = TechnischeWlsException.withCode("code").buildWithMessage("message");

            Mockito.when(eroeffnungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedSaveException).when(eroeffnungsUhrzeitRepository).save(mockedModelAsEntity);
            Mockito.doNothing().when(eroeffnungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setEroeffnungsUhrzeit(modelToSave)).usingRecursiveComparison()
                    .isSameAs(mockedFactoryException);
        }
    }

}
