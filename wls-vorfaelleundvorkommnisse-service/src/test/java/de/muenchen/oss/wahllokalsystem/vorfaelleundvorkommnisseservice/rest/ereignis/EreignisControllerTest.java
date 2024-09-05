package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EreignisControllerTest {

    @Mock
    EreignisService ereignisService;

    @Mock
    EreignisDTOMapper ereignisDTOMapper;

    @InjectMocks
    EreignisController unitUnderTest;

    @Nested
    class GetEreignisse {

        @Test
        void serviceCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val ereignisDTO = TestdataFactory.createEreignisDTOWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);

            Mockito.when(ereignisService.getEreignis(wahlbezirkID)).thenReturn(Optional.of(ereignisModel));
            Mockito.when(ereignisDTOMapper.toDTO(ereignisModel)).thenReturn(ereignisDTO);

            val result = unitUnderTest.getEreignis(wahlbezirkID);

            Assertions.assertThat(result.getBody()).isEqualTo(ereignisDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class PostEreignisse {

        @Test
        void serviceCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisWriteDTO = TestdataFactory.createEreignisWriteDTOWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val ereignisModel = TestdataFactory.createEreignisModelWithData(wahlbezirkID, "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);

            Mockito.when(ereignisDTOMapper.toModel(wahlbezirkID, ereignisWriteDTO)).thenReturn(ereignisModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(wahlbezirkID, ereignisWriteDTO));

            Mockito.verify(ereignisService).postEreignis(ereignisModel);
        }
    }
}
