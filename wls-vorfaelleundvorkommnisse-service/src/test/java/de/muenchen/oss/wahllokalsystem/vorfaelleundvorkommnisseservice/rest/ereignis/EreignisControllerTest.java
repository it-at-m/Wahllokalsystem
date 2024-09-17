package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
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
        void should_return_WahlbezirkEreignisseDTO_when_given_valid_wahlbezirkid() {
            val wahlbezirkID = "wahlbezirkID";
            boolean keineVorfaelle = false;
            boolean keineVorkommnisse = true;
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithEreignisart(Ereignisart.VORFALL));
            val ereignisseModel = TestdataFactory.createEreignisseModelWithData(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelList);

            val wahlbezirkEreignisseDto = TestdataFactory.createWahlbezirkEreignisseDTOFromModel(ereignisseModel);

            Mockito.when(ereignisService.getEreignis(wahlbezirkID)).thenReturn(Optional.of(ereignisseModel));
            Mockito.when(ereignisDTOMapper.toDTO(ereignisseModel)).thenReturn(wahlbezirkEreignisseDto);

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(wahlbezirkEreignisseDto);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_return_no_content_status_when_no_data_found() {
            val wahlbezirkID = "wahlbezirkID";

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostEreignisse {

        @Test
        void should_not_throw_Exception_when_new_data_is_saved() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisDTO> ereignisDtoList = new ArrayList<>();
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData());
            val ereignisseWriteDto = TestdataFactory.createEreignisseWriteDTOWithData(ereignisDtoList);

            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelFromDto(wahlbezirkID, ereignisseWriteDto);

            Mockito.when(ereignisDTOMapper.toModel(wahlbezirkID, ereignisseWriteDto)).thenReturn(ereignisseWriteModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(wahlbezirkID, ereignisseWriteDto));
            Mockito.verify(ereignisService).postEreignis(ereignisseWriteModel);
        }
    }
}
