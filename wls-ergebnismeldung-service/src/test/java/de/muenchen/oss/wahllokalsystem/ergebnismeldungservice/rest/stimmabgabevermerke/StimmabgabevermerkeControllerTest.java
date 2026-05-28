package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
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
public class StimmabgabevermerkeControllerTest {

  @Mock StimmabgabevermerkeService stimmabgabevermerkeService;

  @Mock StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

  @InjectMocks StimmabgabevermerkeController unitUnderTest;

  @Nested
  class GetStimmabgabevermerke {

    @Test
    void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      val mockedServiceResponse =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());
      val mockedServiceResponseAsDTO =
          new StimmabgabevermerkeDTO(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      Mockito.when(
              stimmabgabevermerkeService.getStimmabgabevermerke(
                  wahlbezirkID, wahlID, waehlerverzeichnisNummer))
          .thenReturn(Optional.of(mockedServiceResponse));
      Mockito.when(stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeDTO(mockedServiceResponse))
          .thenReturn(mockedServiceResponseAsDTO);

      val result =
          unitUnderTest.getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      Mockito.when(
              stimmabgabevermerkeService.getStimmabgabevermerke(
                  wahlbezirkID, wahlID, waehlerverzeichnisNummer))
          .thenReturn(Optional.empty());

      val result =
          unitUnderTest.getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      Assertions.assertThat(result.getBody()).isNull();
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
  }

  @Nested
  class PostStimmabgabevermerke {

    @Test
    void should_callServiceWithModel_when_calledWithData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;
      val stimmabgabevermerkeDTO =
          new StimmabgabevermerkeDTO(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());

      val mockedStimmabgabevermerkeModel =
          new StimmabgabevermerkeModel(
              wahlbezirkID,
              wahlID,
              waehlerverzeichnisNummer,
              Collections.emptySet(),
              Collections.emptySet());
      Mockito.when(
              stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeModel(
                  wahlID, wahlbezirkID, waehlerverzeichnisNummer, stimmabgabevermerkeDTO))
          .thenReturn(mockedStimmabgabevermerkeModel);

      unitUnderTest.postStimmabgabevermerke(
          wahlbezirkID, wahlID, waehlerverzeichnisNummer, stimmabgabevermerkeDTO);

      Mockito.verify(stimmabgabevermerkeService)
          .postStimmabgabevermerke(eq(mockedStimmabgabevermerkeModel));
    }
  }
}
