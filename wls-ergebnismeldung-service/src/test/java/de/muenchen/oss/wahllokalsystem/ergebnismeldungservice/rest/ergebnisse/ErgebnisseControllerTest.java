package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.BezirkUndWahlIDStapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import java.util.Collections;
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
class ErgebnisseControllerTest {

    @Mock
    ErgebnisseService ergebnisseService;

    @Mock
    ErgebnisseDTOMapper ergebnisseDTOMapper;

    @InjectMocks
    ErgebnisseController unitUnderTest;

    @Nested
    class GetErgebnisse {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelartDTO = StapelartDTO.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;
            val stapelart = Stapelart.LTW_BZW_A;

            val mockedErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);
            val bezirkUndWahlIDStapelartDTO = new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO);
            val mockedServiceResponse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, Collections.emptyList());
            val mockedServiceResponseAsDTO = new ErgebnisseDTO(bezirkUndWahlIDStapelartDTO, Collections.emptyList());

            Mockito.when(ergebnisseService.getErgebnisse(mockedErgebnisseReference)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(ergebnisseDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);
            Mockito.when(ergebnisseDTOMapper.toSpapelart(stapelartDTO)).thenReturn(stapelart);

            val result = unitUnderTest.getErgebnisse(wahlbezirkID, wahlID, stapelartDTO);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val mockedErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(ergebnisseService.getErgebnisse(mockedErgebnisseReference)).thenReturn(Optional.empty());
            Mockito.when(ergebnisseDTOMapper.toSpapelart(stapelartDTO)).thenReturn(stapelart);
            val result = unitUnderTest.getErgebnisse(wahlbezirkID, wahlID, stapelartDTO);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class GetAllErgebnisse {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val bezirkUndWahlIDStapelartDTO = new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO);

            val mockedServiceResponse = new ErgebnisseModel(wahlbezirkID, wahlID, null, Collections.emptyList());
            val mockedListServiceResponse = List.of(mockedServiceResponse);
            val mockedServiceResponseAsDTO = new ErgebnisseDTO(bezirkUndWahlIDStapelartDTO, Collections.emptyList());
            val mockedListServiceResponseAsDTO = List.of(mockedServiceResponseAsDTO);

            Mockito.when(ergebnisseService.getAllErgebnisse(wahlbezirkID, wahlID)).thenReturn(mockedListServiceResponse);
            Mockito.when(ergebnisseDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getAllErgebnisse(wahlbezirkID, wahlID);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedListServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(ergebnisseService.getAllErgebnisse(wahlbezirkID, wahlID)).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getAllErgebnisse(wahlbezirkID, wahlID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostErgebnisse {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelartDTO = StapelartDTO.LTW_BZW_A;
            val stapelartModel = StapelartModel.LTW_BZW_A;
            val stapelart = Stapelart.LTW_BZW_A;

            val ergebnisseDTO = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO), Collections.emptyList());
            val ergebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            val mockedErgebnisseModel = new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, Collections.emptyList());
            Mockito.when(ergebnisseDTOMapper.toModel(ergebnisseDTO)).thenReturn(mockedErgebnisseModel);
            Mockito.when(ergebnisseDTOMapper.toSpapelart(stapelartDTO)).thenReturn(stapelart);

            unitUnderTest.postErgebnisse(wahlbezirkID, wahlID, stapelartDTO, ergebnisseDTO);

            Mockito.verify(ergebnisseService).postErgebnisse(ergebnisseReference,
                    new ErgebnisseModel(wahlbezirkID, wahlID, stapelartModel, Collections.emptyList()));
        }
    }
}
