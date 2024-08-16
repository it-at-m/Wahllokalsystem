package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenService;
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
class KopfdatenControllerTest {

    @Mock
    KopfdatenService kopfdatenService;

    @Mock
    KopfdatenDTOMapper kopfdatenDTOMapper;

    @InjectMocks
    KopfdatenController kopfdatenController;

    @Nested
    class GetKopfdaten {

        @Test
        void serviceIsCalledAndObjectsAreMapped() {

            val kopfdatenModel = KopfdatenModel.builder().build();
            val kopfdatenDTO = KopfdatenDTO.builder().build();

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Mockito.when(kopfdatenService.getKopfdaten(bezirkUndWahlID)).thenReturn(kopfdatenModel);
            Mockito.when(kopfdatenDTOMapper.toDTO(kopfdatenModel)).thenReturn(kopfdatenDTO);

            val result = kopfdatenController.getKopfdaten(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID());

            Assertions.assertThat(result).isEqualTo(kopfdatenDTO);
        }
    }
}
