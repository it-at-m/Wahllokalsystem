package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import java.util.Collections;
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
            val stapelart = Stapelart.LTW_BZW_A;

            val mockedErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);
            val bezirkUndWahlIDStapelart = new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart);
            val mockedServiceResponse = new ErgebnisseModel(wahlbezirkID, wahlID, stapelart, Collections.emptyList());
            val mockedServiceResponseAsDTO = new ErgebnisseDTO(bezirkUndWahlIDStapelart, Collections.emptyList());

            Mockito.when(ergebnisseDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart)).thenReturn(mockedErgebnisseReference);
            Mockito.when(ergebnisseService.getErgebnisse(mockedErgebnisseReference)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(ergebnisseDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getErgebnisse(wahlbezirkID, wahlID, stapelart);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;

            val mockedErgebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(ergebnisseDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart)).thenReturn(mockedErgebnisseReference);
            Mockito.when(ergebnisseService.getErgebnisse(mockedErgebnisseReference)).thenReturn(null);

            val result = unitUnderTest.getErgebnisse(wahlbezirkID, wahlID, stapelart);

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
            val stapelart = Stapelart.LTW_BZW_A;
            val ergebnisseDTO = new ErgebnisseDTO(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart), Collections.emptyList());
            val ergebnisseReference = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);

            val mockedErgebnisseModel = new ErgebnisseModel(wahlbezirkID, wahlID, stapelart, Collections.emptyList());
            Mockito.when(ergebnisseDTOMapper.toModel(ergebnisseDTO)).thenReturn(mockedErgebnisseModel);

            unitUnderTest.postErgebnisse(wahlbezirkID, wahlID, stapelart, ergebnisseDTO);

            Mockito.verify(ergebnisseService).postErgebnisse(ergebnisseReference,
                    new ErgebnisseModel(wahlbezirkID, wahlID, stapelart, Collections.emptyList()));
        }
    }
}
