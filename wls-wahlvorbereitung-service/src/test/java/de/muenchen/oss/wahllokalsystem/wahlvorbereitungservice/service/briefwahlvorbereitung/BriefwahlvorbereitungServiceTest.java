package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Briefwahlvorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.BriefwahlvorbereitungRepository;
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
class BriefwahlvorbereitungServiceTest {

    @Mock
    BriefwahlvorbereitungRepository briefwahlvorbereitungRepository;

    @Mock
    BriefwahlvorbereitungModelMapper briefwahlvorbereitungModelMapper;

    @Mock
    BriefwahlvorbereitungValidator briefwahlvorbereitungValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    BriefwahlvorbereitungService unitUnderTest;

    @Nested
    class getBriefwahlvorbereitung {
        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new Briefwahlvorbereitung();
            val mockedMappedRepoResponseAsModel = BriefwahlvorbereitungModel.builder().build();

            Mockito.doNothing().when(briefwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(briefwahlvorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(briefwahlvorbereitungModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getBriefwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(briefwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(briefwahlvorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getBriefwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(briefwahlvorbereitungModelMapper, times(0)).toModel(any());
        }

        @Test
        void exceptionFromValidator() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(briefwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getBriefwahlvorbereitung(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

    @Nested
    class SetBriefwahlvorbereitung {

        @Test
        void isSaved() {
            val modelToSave = BriefwahlvorbereitungModel.builder().build();

            val mockedModelAsEntity = new Briefwahlvorbereitung();
            Mockito.when(briefwahlvorbereitungModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);

            Mockito.doNothing().when(briefwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);

            unitUnderTest.setBriefwahlvorbereitung(modelToSave);

            Mockito.verify(briefwahlvorbereitungRepository).save(mockedModelAsEntity);
        }

        @Test
        void exceptionFromValidationGotThrown() {
            val modelToSave = BriefwahlvorbereitungModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(briefwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setBriefwahlvorbereitung(modelToSave)).isSameAs(mockedValidationException);
        }

        @Test
        void exceptionFromSavingIsMapped() {
            val modelToSave = BriefwahlvorbereitungModel.builder().build();

            val mockedModelAsEntity = new Briefwahlvorbereitung();
            val mockedSaveException = new RuntimeException("fail on save");
            val mockedFactoryException = TechnischeWlsException.withCode("code").buildWithMessage("message");

            Mockito.when(briefwahlvorbereitungModelMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedSaveException).when(briefwahlvorbereitungRepository).save(mockedModelAsEntity);
            Mockito.doNothing().when(briefwahlvorbereitungValidator).validModelToSetOrThrow(modelToSave);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE)).thenReturn(mockedFactoryException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setBriefwahlvorbereitung(modelToSave)).usingRecursiveComparison()
                    .isSameAs(mockedFactoryException);
        }
    }

}
