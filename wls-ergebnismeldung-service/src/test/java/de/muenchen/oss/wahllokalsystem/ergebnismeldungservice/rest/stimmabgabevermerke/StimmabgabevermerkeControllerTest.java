package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
public class StimmabgabevermerkeControllerTest {

    @Mock
    StimmabgabevermerkeService stimmabgabevermerkeService;

    @Mock
    StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

    @InjectMocks
    StimmabgabevermerkeController unitUnderTest;

    @Nested
    class GetStimmabgabevermerke {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;

            val bezirkUndWaehlerverzeichnisNummer = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);
            val mockedServiceResponse = new StimmabgabevermerkeModel(bezirkUndWaehlerverzeichnisNummer, anzahlBlaetter, null);
            val mockedServiceResponseAsDTO = new StimmabgabevermerkeDTO(wahlbezirkID, waehlerverzeichnisNummer, anzahlBlaetter, null);

            Mockito.when(stimmabgabevermerkeService.getStimmabgabevermerke(bezirkUndWaehlerverzeichnisNummer))
                    .thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeDTO(mockedServiceResponse))
                    .thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;

            val bezirkUndWaehlerverzeichnisNummer = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);

            Mockito.when(stimmabgabevermerkeService.getStimmabgabevermerke(bezirkUndWaehlerverzeichnisNummer)).thenReturn(Optional.empty());

            val result = unitUnderTest.getStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostStimmabgabevermerke {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;
            val stimmabgabevermerkeDTO = new StimmabgabevermerkeDTO(wahlbezirkID, waehlerverzeichnisNummer, anzahlBlaetter, null);

            val mockedStimmabgabevermerkeModel = new StimmabgabevermerkeModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    anzahlBlaetter, null);
            Mockito.when(stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeModel(stimmabgabevermerkeDTO)).thenReturn(mockedStimmabgabevermerkeModel);

            unitUnderTest.postStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer, stimmabgabevermerkeDTO);

            Mockito.verify(stimmabgabevermerkeService).postStimmabgabevermerke(
                    eq(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)), eq(mockedStimmabgabevermerkeModel));
        }
    }
}
