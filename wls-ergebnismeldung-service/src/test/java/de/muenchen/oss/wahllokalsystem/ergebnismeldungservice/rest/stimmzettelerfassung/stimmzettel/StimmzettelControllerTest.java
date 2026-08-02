package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class StimmzettelControllerTest {

  @Mock StimmzettelDTOMapper stimmzettelDTOMapper;

  @Mock StimmzettelService stimmzettelService;

  @InjectMocks StimmzettelController unitUnderTest;

  @Captor ArgumentCaptor<List<StimmzettelOfTeamModel>> saveStimmzettelStimmzettelListCaptor;

  @Nested
  class GetStimmzettel {

    @Test
    void should_returnListWithHttpStatusOk_when_serviceReturnsData() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val mockedServiceStimmzettel = Mockito.mock(StimmzettelOfTeamModel.class);
      Mockito.when(
              stimmzettelService.getStimmzettel(
                  new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)))
          .thenReturn(List.of(mockedServiceStimmzettel));

      val mockedMappedValue = Instancio.create(StimmzettelOfTeamDTO.class);
      Mockito.when(stimmzettelDTOMapper.toDTO(mockedServiceStimmzettel))
          .thenReturn(mockedMappedValue);

      val result = unitUnderTest.getStimmzettel(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      Assertions.assertThat(result.getBody()).isEqualTo(List.of(mockedMappedValue));
    }

    @Test
    void should_returnNoDataWithHttpStatusCodeNoContent_when_serviceReturnsEmptyList() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      Mockito.when(
              stimmzettelService.getStimmzettel(
                  new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)))
          .thenReturn(Collections.emptyList());

      val result = unitUnderTest.getStimmzettel(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();

      Mockito.verifyNoInteractions(stimmzettelDTOMapper);
    }
  }

  @Nested
  class PostStimmzettel {

    @Test
    void should_callServiceWithMappedValues_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);
      val sizeOfList = Instancio.gen().ints().max(1000).min(1).get();
      val requestBody = Instancio.ofList(StimmzettelOfTeamDTO.class).size(sizeOfList).create();

      Mockito.when(stimmzettelDTOMapper.toModel(Mockito.any()))
          .then(invocation -> Instancio.create(StimmzettelOfTeamModel.class));

      unitUnderTest.postStimmzettel(wahlID, wahlbezirkID, teamID, requestBody);

      Mockito.verify(stimmzettelService)
          .saveStimmzettel(
              Mockito.eq(new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)),
              saveStimmzettelStimmzettelListCaptor.capture());
      Assertions.assertThat(saveStimmzettelStimmzettelListCaptor.getAllValues()).hasSize(1);
      Assertions.assertThat(saveStimmzettelStimmzettelListCaptor.getValue()).hasSize(sizeOfList);
      Assertions.assertThat(saveStimmzettelStimmzettelListCaptor.getValue())
          .satisfies(
              stimmzettelToSaveListItem ->
                  Assertions.assertThat(stimmzettelToSaveListItem).isNotNull());
    }
  }

  @Nested
  class GetAnzahlStimmzettel {

    @Test
    void should_returnResponseOfService_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      val anzahlStimmzettel = Instancio.gen().ints().max(1000).min(1).get();
      Mockito.when(
              stimmzettelService.getAnzahlStimmzettel(new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .thenReturn(anzahlStimmzettel);

      val result = unitUnderTest.getAnzahlStimmzettel(wahlID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(anzahlStimmzettel);
    }
  }
}
