package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.http.HttpStatus;

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
            boolean keineVorfaelle = false;
            boolean keineVorkommnisse = true;
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseModel = TestdataFactory.createEreignisseModelWithData(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelList);

            val wahlbezirkEreignisseDto = TestdataFactory.createWahlbezirkEreignisseDTOFromModel(ereignisseModel);

            Mockito.when(ereignisService.getEreignis(wahlbezirkID)).thenReturn(Optional.of(ereignisseModel));
            Mockito.when(ereignisDTOMapper.toDTO(ereignisseModel)).thenReturn(wahlbezirkEreignisseDto);

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(wahlbezirkEreignisseDto);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class PostEreignisse {

        @Test
        void serviceCalled() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisDTO> ereignisDtoList = new ArrayList<>();
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseWriteDto = TestdataFactory.createEreignisseWriteDTOWithData(ereignisDtoList);

            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelFromDto(wahlbezirkID, ereignisseWriteDto);

            Mockito.when(ereignisDTOMapper.toModel(wahlbezirkID, ereignisseWriteDto)).thenReturn(ereignisseWriteModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(wahlbezirkID, ereignisseWriteDto));
            Mockito.verify(ereignisService).postEreignis(ereignisseWriteModel);
        }
    }
}
