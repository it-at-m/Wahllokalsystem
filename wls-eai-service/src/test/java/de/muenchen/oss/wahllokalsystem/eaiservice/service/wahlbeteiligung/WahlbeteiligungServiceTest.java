package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.Wahlbeteiligung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.WahlbeteiligungRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
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
class WahlbeteiligungServiceTest {

    @Mock
    WahlbeteiligungMapper wahlbeteiligungMapper;

    @Mock
    WahlbeteiligungValidator wahlbeteiligungValidator;

    @Mock
    WahlbeteiligungRepository wahlbeteiligungRepository;

    @InjectMocks
    WahlbeteiligungService unitUnderTest;

    @Nested
    class SaveWahlbeteiligung {

        @Test
        void isSaved() {
            val modelToSave = WahlbeteiligungsMeldungDTO.builder().build();
            val mockedModelAsEntity = new Wahlbeteiligung();

            Mockito.when(wahlbeteiligungMapper.toEntity(modelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doNothing().when(wahlbeteiligungValidator).validDTOToSetOrThrow(modelToSave);

            unitUnderTest.saveWahlbeteiligung(modelToSave);

            Mockito.verify(wahlbeteiligungRepository).save(mockedModelAsEntity);
        }

        @Test
        void exceptionFromValidationGotThrown() {
            val modelToSave = WahlbeteiligungsMeldungDTO.builder().build();
            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahlbeteiligungValidator).validDTOToSetOrThrow(modelToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.saveWahlbeteiligung(modelToSave)).isSameAs(mockedValidationException);
        }

    }
}
