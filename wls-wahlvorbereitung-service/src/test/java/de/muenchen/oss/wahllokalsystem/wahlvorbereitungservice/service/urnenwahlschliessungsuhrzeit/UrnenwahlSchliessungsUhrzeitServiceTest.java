package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
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
class UrnenwahlSchliessungsUhrzeitServiceTest {

    @Mock
    UrnenwahlSchliessungsUhrzeitRepository urnenwahlSchliessungsUhrzeitRepository;

    @Mock
    UrnenwahlSchliessungsUhrzeitModelMapper urnenwahlSchliessungsUhrzeitModelMapper;

    @Mock
    UrnenwahlSchliessungsUhrzeitValidator urnenwahlSchliessungsUhrzeitValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UrnenwahlSchliessungsUhrzeitService unitUnderTest;

    @Nested
    class getUrnenwahlSchliessungsUhrzeit {
        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new UrnenwahlSchliessungsUhrzeit();
            val mockedMappedRepoResponseAsModel = UrnenwahlSchliessungsUhrzeitModel.builder().build();

            Mockito.doNothing().when(urnenwahlSchliessungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(urnenwahlSchliessungsUhrzeitModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(urnenwahlSchliessungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(urnenwahlSchliessungsUhrzeitModelMapper, times(0)).toModel(any());
        }

        @Test
        void exceptionFromValidator() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(urnenwahlSchliessungsUhrzeitValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

    @Nested
    class SetUrnenwahlSchliessungsUhrzeit {

        @Test
        void isSaved() {
            val modelToSave = UrnenwahlSchliessungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new UrnenwahlSchliessungsUhrzeit();
            Mockito.when(urnenwahlSchliessungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);

            Mockito.doNothing().when(urnenwahlSchliessungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSave);

            Mockito.verify(urnenwahlSchliessungsUhrzeitRepository).save(mockedModelAsEntity);
        }

        @Test
        void exceptionFromValidationGotThrown() {
            val modelToSave = UrnenwahlSchliessungsUhrzeitModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(urnenwahlSchliessungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionFromSavingIsMapped() {
            val modelToSave = UrnenwahlSchliessungsUhrzeitModel.builder().build();

            val mockedModelAsEntity = new UrnenwahlSchliessungsUhrzeit();
            val mockedSaveException = new RuntimeException("fail on save");
            val mockedFactoryException = TechnischeWlsException.withCode("code").buildWithMessage("message");

            Mockito.when(urnenwahlSchliessungsUhrzeitModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedSaveException).when(urnenwahlSchliessungsUhrzeitRepository).save(mockedModelAsEntity);
            Mockito.doNothing().when(urnenwahlSchliessungsUhrzeitValidator).validModelToSetOrThrow(modelToSave);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlSchliessungsUhrzeit(modelToSave)).usingRecursiveComparison()
                    .isSameAs(mockedFactoryException);
        }
    }

}
