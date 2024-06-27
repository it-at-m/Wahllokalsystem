package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class WahlvorschlaegeControllerTest {

    @Mock
    WahlvorschlaegeService wahlvorschlaegeService;

    @Mock
    WahlvorschlaegeDTOMapper wahlvorschlaegeDTOMapper;

    @InjectMocks
    WahlvorschlaegeController wahlvorschlaegeController;

    @Nested
    class GetWahlvorschlaege {

        @Test
        void serviceIsCalledAndObjectsAreMapped() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val wahlvorschlaegeModel = WahlvorschlaegeModel.builder().build();
            val wahlvorschlaegeDTO = WahlvorschlaegeDTO.builder().build();

            Mockito.when(wahlvorschlaegeService.getWahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(wahlvorschlaegeModel);
            Mockito.when(wahlvorschlaegeDTOMapper.toDTO(wahlvorschlaegeModel)).thenReturn(wahlvorschlaegeDTO);

            val result = wahlvorschlaegeController.getWahlvorschlaege(wahlID, wahlbezirkID);

            Assertions.assertThat(result).isEqualTo(wahlvorschlaegeDTO);
        }
    }
}
