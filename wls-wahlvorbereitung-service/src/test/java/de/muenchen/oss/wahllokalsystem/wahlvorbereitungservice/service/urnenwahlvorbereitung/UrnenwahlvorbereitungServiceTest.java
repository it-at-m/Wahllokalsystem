package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitungRepository;
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
class UrnenwahlvorbereitungServiceTest {

    @Mock
    UrnenwahlVorbereitungRepository urnenwahlVorbereitungRepository;

    @Mock
    UrnenwahlvorbereitungModelMapper urnenwahlvorbereitungModelMapper;

    @Mock
    UrnenwahlvorbereitungValidator urnenwahlvorbereitungValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UrnenwahlvorbereitungService unitUnderTest;

    @Nested
    class getUrnenwahlvorbereitung {
        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new UrnenwahlVorbereitung();
            val mockedMappedRepoResponseAsModel = UrnenwahlvorbereitungModel.builder().build();

            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(urnenwahlvorbereitungModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(urnenwahlvorbereitungModelMapper, times(0)).toModel(any());
        }

        @Test
        void exceptionFromValidator() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

    @Nested
    class SetUrnenwahlvorbereitung {

        @Test
        void isSaved() {
            val modelToSave = UrnenwahlvorbereitungModel.builder().build();

            val mockedModelAsEntity = new UrnenwahlVorbereitung();
            Mockito.when(urnenwahlvorbereitungModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);

            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);

            unitUnderTest.setUrnenwahlvorbereitung(modelToSave);

            Mockito.verify(urnenwahlVorbereitungRepository).save(mockedModelAsEntity);
        }

        @Test
        void exceptionFromValidationGotThrown() {
            val modelToSave = UrnenwahlvorbereitungModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(urnenwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionFromSavingIsMapped() {
            val modelToSave = UrnenwahlvorbereitungModel.builder().build();

            val mockedModelAsEntity = new UrnenwahlVorbereitung();
            val mockedSaveException = new RuntimeException("fail on save");
            val mockedFactoryException = TechnischeWlsException.withCode("code").buildWithMessage("message");

            Mockito.when(urnenwahlvorbereitungModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedSaveException).when(urnenwahlVorbereitungRepository).save(mockedModelAsEntity);
            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setUrnenwahlvorbereitung(modelToSave)).usingRecursiveComparison()
                    .isSameAs(mockedFactoryException);
        }
    }

}
