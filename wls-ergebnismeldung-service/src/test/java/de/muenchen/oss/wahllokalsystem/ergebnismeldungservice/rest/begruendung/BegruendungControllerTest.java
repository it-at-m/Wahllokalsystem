package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungService;
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
class BegruendungControllerTest {

    @Mock
    BegruendungService begruendungService;

    @Mock
    BegruendungDTOMapper begruendungDTOMapper;

    @InjectMocks
    BegruendungController unitUnderTest;

    @Nested
    class GetBegruendung {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;

            val mockedBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);
            val bezirkUndWahlIDStapelart = new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart);
            val mockedServiceResponse = new BegruendungModel(wahlbezirkID, wahlID, stapelart, null, null, true, true);
            val mockedServiceResponseAsDTO = new BegruendungDTO(bezirkUndWahlIDStapelart, null, null, true, true);

            Mockito.when(begruendungDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart)).thenReturn(mockedBegruendungReference);
            Mockito.when(begruendungService.getBegruendung(mockedBegruendungReference)).thenReturn(mockedServiceResponse);
            Mockito.when(begruendungDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getBegruendung(wahlbezirkID, wahlID, stapelart);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;

            val mockedBegruendungReference = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            Mockito.when(begruendungDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart)).thenReturn(mockedBegruendungReference);
            Mockito.when(begruendungService.getBegruendung(mockedBegruendungReference)).thenReturn(null);

            val result = unitUnderTest.getBegruendung(wahlbezirkID, wahlID, stapelart);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostBegruendung {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;
            val begruendungDTO = new BegruendungDTO(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart), null, null, true, true);
            val begruendungReferce = new BegruendungReference(wahlbezirkID, wahlID, stapelart);

            val mockedBegruendungModel = new BegruendungModel(wahlbezirkID, wahlID, stapelart, null, null, true, true);
            Mockito.when(begruendungDTOMapper.toModel(begruendungDTO)).thenReturn(mockedBegruendungModel);

            unitUnderTest.postBegruendung(wahlbezirkID, wahlID, stapelart, begruendungDTO);

            Mockito.verify(begruendungService).postBegruendung(new BegruendungModel(wahlbezirkID, wahlID, stapelart, null, null, true, true),
                    begruendungReferce);
        }
    }
}
