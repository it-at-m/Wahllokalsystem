package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
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

            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORFALL));
            val mockedEreignisseModel = TestdataFactory.CreateEreignisseModel.withData(wahlbezirkID, keineVorfaelle, keineVorkommnisse,
                    mockedEreignisModelList);
            val expectedWahlbezirkEreignisseDto = TestdataFactory.CreateWahlbezirkEreignisseDto.fromModel(mockedEreignisseModel);

            Mockito.when(ereignisService.getEreignis(wahlbezirkID)).thenReturn(Optional.of(mockedEreignisseModel));
            Mockito.when(ereignisDTOMapper.toDTO(mockedEreignisseModel)).thenReturn(expectedWahlbezirkEreignisseDto);

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(expectedWahlbezirkEreignisseDto);
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
        void should_not_throw_exception_when_new_data_is_saved() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisDtoList = List.of(TestdataFactory.CreateEreignisDto.withData());
            val mockedEreignisseWriteDto = TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.fromDto(wahlbezirkID, mockedEreignisseWriteDto);

            Mockito.when(ereignisDTOMapper.toModel(wahlbezirkID, mockedEreignisseWriteDto)).thenReturn(mockedEreignisseWriteModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(wahlbezirkID, mockedEreignisseWriteDto));
            Mockito.verify(ereignisService).postEreignis(mockedEreignisseWriteModel);
        }
    }
}
