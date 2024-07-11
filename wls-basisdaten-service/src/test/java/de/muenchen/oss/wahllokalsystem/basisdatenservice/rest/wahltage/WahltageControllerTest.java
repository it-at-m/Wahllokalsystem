package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import java.util.List;
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
class WahltageControllerTest {

    @Mock
    WahltageService wahltageService;

    @Mock
    WahltageDTOMapper wahltageDTOMapper;

    @InjectMocks
    WahltageController wahltageController;

    @Nested
    class GetWahltage {

        @Test
        void serviceIsCalledAndObjectsAreMapped() {

            List<WahltagModel> wahltagModels = List.of(
                    WahltagModel.builder().build(),
                    WahltagModel.builder().build(),
                    WahltagModel.builder().build());

            List<WahltagDTO> wahltagDTOs = List.of(
                    WahltagDTO.builder().build(),
                    WahltagDTO.builder().build(),
                    WahltagDTO.builder().build());

            Mockito.when(wahltageService.getWahltage()).thenReturn(wahltagModels);
            Mockito.when(wahltageDTOMapper.fromListOfWahltagModelToListOfWahltagDTO(wahltagModels)).thenReturn(wahltagDTOs);

            val result = wahltageController.getWahltage();

            Assertions.assertThat(result.getBody()).isEqualTo(wahltagDTOs);
        }
    }
}
