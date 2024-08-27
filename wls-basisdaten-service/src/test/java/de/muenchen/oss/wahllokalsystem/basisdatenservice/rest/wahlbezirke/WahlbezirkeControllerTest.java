package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import static org.mockito.ArgumentMatchers.any;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkeService;
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
class WahlbezirkeControllerTest {

    @Mock
    WahlbezirkeService wahlbezirkeService;

    @Mock
    WahlbezirkDTOMapper wahlbezirkDTOMapper;

    @InjectMocks
    WahlbezirkeController wahlbezirkeController;

    @Nested
    class GetWahlbezirke {

        @Test
        void serviceIsCalledAndObjectsAreMapped() {
            val wahlbezirkModels = List.of(
                    WahlbezirkModel.builder().build(),
                    WahlbezirkModel.builder().build(),
                    WahlbezirkModel.builder().build());

            val wahlbezirkDTOs = List.of(
                    WahlbezirkDTO.builder().build(),
                    WahlbezirkDTO.builder().build(),
                    WahlbezirkDTO.builder().build());

            Mockito.when(wahlbezirkeService.getWahlbezirke(any())).thenReturn(wahlbezirkModels);
            Mockito.when(wahlbezirkDTOMapper.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(wahlbezirkModels)).thenReturn(wahlbezirkDTOs);

            val result = wahlbezirkeController.getWahlbezirke("wahltagID");

            Assertions.assertThat(result).isEqualTo(wahlbezirkDTOs);
        }
    }
}
