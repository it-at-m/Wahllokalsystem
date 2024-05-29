package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.Wahlbriefdaten;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.WahlbriefdatenRepository;
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
class WahlbriefdatenServiceTest {

    @Mock
    WahlbriefdatenRepository wahlbriefdatenRepository;

    @Mock
    WahlbriefdatenModelMapper wahlbriefdatenModelMapper;

    @Mock
    WahlbriefdatenValidator wahlbriefdatenValidator;

    @InjectMocks
    WahlbriefdatenService unitUnderTest;

    @Nested
    class GetWahlbriefdaten {

        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoEntity = new Wahlbriefdaten();
            val mockedMappedRepoEntityAsModel = WahlbriefdatenModel.builder().build();

            Mockito.doNothing().when(wahlbriefdatenValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(wahlbriefdatenRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoEntity));
            Mockito.when(wahlbriefdatenModelMapper.toModel(mockedRepoEntity)).thenReturn(mockedMappedRepoEntityAsModel);

            val result = unitUnderTest.getWahlbriefdaten(wahlbezirkID);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedRepoEntityAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(wahlbriefdatenValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(wahlbriefdatenRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlbriefdaten(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationFailed() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlbriefdatenValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbriefdaten(wahlbezirkID)).isSameAs(mockedValidationException);

        }

    }

    @Nested
    class SetWahlbriefdaten {

        @Test
        void newDataSet() {
            val wahlbriefdatenModelToSet = WahlbriefdatenModel.builder().build();

            val mockedModelMappedAsEntity = new Wahlbriefdaten();

            Mockito.doNothing().when(wahlbriefdatenValidator).validWahlbriefdatenToSetOrThrow(wahlbriefdatenModelToSet);
            Mockito.when(wahlbriefdatenModelMapper.toEntity(wahlbriefdatenModelToSet)).thenReturn(mockedModelMappedAsEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setWahlbriefdaten(wahlbriefdatenModelToSet));

            Mockito.verify(wahlbriefdatenRepository).save(mockedModelMappedAsEntity);
        }

        @Test
        void validationFailed() {
            val wahlbriefdatenModelToSet = WahlbriefdatenModel.builder().build();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlbriefdatenValidator).validWahlbriefdatenToSetOrThrow(wahlbriefdatenModelToSet);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWahlbriefdaten(wahlbriefdatenModelToSet)).isSameAs(mockedValidationException);

            Mockito.verifyNoInteractions(wahlbriefdatenRepository);

        }

    }

}
