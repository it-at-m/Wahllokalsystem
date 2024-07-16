package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeitRepository;
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
class UnterbrechungsUhrzeitServiceTest {

    @Mock
    UnterbrechungsUhrzeitRepository unterbrechungsUhrzeitRepository;

    @Mock
    UnterbrechungsUhrzeitModelMapper unterbrechungsUhrzeitModelMapper;

    @Mock
    UnterbrechungsUhrzeitValidator unterbrechungsUhrzeitValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UnterbrechungsUhrzeitService unitUnderTest;

    @Nested
    class getUnterbrechungsUhrzeit {
        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new UnterbrechungsUhrzeit();
            val mockedMappedRepoResponseAsModel = UnterbrechungsUhrzeitModel.builder().build();

            Mockito.doNothing().when(unterbrechungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(unterbrechungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(unterbrechungsUhrzeitModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(unterbrechungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(unterbrechungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(unterbrechungsUhrzeitModelMapper, times(0)).toModel(any());
        }

        @Test
        void exceptionFromValidator() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(unterbrechungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUnterbrechungsUhrzeit(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

    @Nested
    class SetUnterbrechungsUhrzeit {

        @Test
        void isSaved() {
            val modelToSave = UnterbrechungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new UnterbrechungsUhrzeit();
            Mockito.when(unterbrechungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);

            Mockito.doNothing().when(unterbrechungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            unitUnderTest.setUnterbrechungsUhrzeit(modelToSave);

            Mockito.verify(unterbrechungsUhrzeitRepository).save(mockedModelAsEntity);
        }

        @Test
        void exceptionFromValidationGotThrown() {
            val modelToSave = UnterbrechungsUhrzeitModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(unterbrechungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionFromSavingIsMapped() {
            val modelToSave = UnterbrechungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new UnterbrechungsUhrzeit();
            val mockedSaveException = new RuntimeException("fail on save");
            val mockedFactoryException = TechnischeWlsException.withCode("code").buildWithMessage("message");

            Mockito.when(unterbrechungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedSaveException).when(unterbrechungsUhrzeitRepository).save(mockedModelAsEntity);
            Mockito.doNothing().when(unterbrechungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUnterbrechungsUhrzeit(modelToSave)).usingRecursiveComparison()
                    .isSameAs(mockedFactoryException);
        }
    }

}
