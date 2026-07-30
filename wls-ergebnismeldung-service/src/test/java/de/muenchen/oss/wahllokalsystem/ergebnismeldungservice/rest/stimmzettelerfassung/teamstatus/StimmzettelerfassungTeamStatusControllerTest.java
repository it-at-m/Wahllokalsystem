package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.TeamStatusService;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class StimmzettelerfassungTeamStatusControllerTest {

  @Mock TeamStatusService teamStatusService;

  @Mock ErfassungTeamStatusDTOMapper erfassungTeamStatusDTOMapper;
  @Mock ErfassungTeamStatusEntryDTOMapper erfassungTeamStatusEntryDTOMapper;

  @InjectMocks private StimmzettelerfassungTeamStatusController underTest;

  @Nested
  class SaveStimmzettelerfassungTeamStatus {

    @Test
    void should_callServiceWithMappedRequest_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);
      val requestBody = Instancio.create(StimmzettelerfassungTeamStatusDTO.class);

      val mockedMappedStatus = Instancio.create(ErfassungTeamStatusModel.class);
      Mockito.when(erfassungTeamStatusDTOMapper.toModel(requestBody.status()))
          .thenReturn(mockedMappedStatus);

      underTest.saveStimmzettelerfassungTeamStatus(wahlID, wahlbezirkID, teamID, requestBody);

      Mockito.verify(teamStatusService)
          .saveTeamStatus(
              new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID), mockedMappedStatus);
    }
  }

  @Nested
  class GetStimmzettelerfassungTeamStatus {

    @Test
    void should_returnOkWithResponse_when_serviceHasData() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val mockedServiceResponse = Instancio.create(ErfassungTeamStatusModel.class);
      Mockito.when(
              teamStatusService.getTeamStatus(
                  new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)))
          .thenReturn(Optional.of(mockedServiceResponse));

      val mockedMappeServiceResponse = Instancio.create(ErfassungTeamStatusDTO.class);
      Mockito.when(erfassungTeamStatusDTOMapper.toDTO(mockedServiceResponse))
          .thenReturn(mockedMappeServiceResponse);

      val result = underTest.getStimmzettelerfassungTeamStatus(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      Assertions.assertThat(result.getBody()).isNotNull();
      Assertions.assertThat(result.getBody().status()).isEqualTo(mockedMappeServiceResponse);
    }

    @Test
    void should_returnNoContentWithNoResponse_when_serviceHasNoData() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      Mockito.when(
              teamStatusService.getTeamStatus(
                  new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)))
          .thenReturn(Optional.empty());

      val result = underTest.getStimmzettelerfassungTeamStatus(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();
    }
  }

  @Nested
  class GetStimmzettelerfassungTeamStatusList {

    @Test
    void should_return_ok_with_list_when_service_has_data() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      val model1 =
          new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung
              .teamstatus.ErfassungTeamStatusEntryModel(
              Instancio.create(String.class), ErfassungTeamStatusModel.IN_BEARBEITUNG);
      val model2 =
          new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung
              .teamstatus.ErfassungTeamStatusEntryModel(
              Instancio.create(String.class), ErfassungTeamStatusModel.ABGESCHLOSSEN);

      Mockito.when(teamStatusService.getTeamStatusList(Mockito.any()))
          .thenReturn(java.util.List.of(model1, model2));

      val dto1 =
          new StimmzettelerfassungTeamStatusEntryDTO(
              model1.teamID(), ErfassungTeamStatusDTO.IN_BEARBEITUNG);
      val dto2 =
          new StimmzettelerfassungTeamStatusEntryDTO(
              model2.teamID(), ErfassungTeamStatusDTO.ABGESCHLOSSEN);
      Mockito.when(erfassungTeamStatusEntryDTOMapper.toDTO(model1)).thenReturn(dto1);
      Mockito.when(erfassungTeamStatusEntryDTOMapper.toDTO(model2)).thenReturn(dto2);

      val result = underTest.getStimmzettelerfassungTeamStatusList(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      Assertions.assertThat(result.getBody()).hasSize(2);
      Assertions.assertThat(result.getBody()).containsExactlyInAnyOrder(dto1, dto2);
    }

    @Test
    void should_return_no_content_when_service_returns_empty_list() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      Mockito.when(teamStatusService.getTeamStatusList(Mockito.any()))
          .thenReturn(java.util.Collections.emptyList());

      val result = underTest.getStimmzettelerfassungTeamStatusList(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void should_return_no_content_when_service_returns_null() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      Mockito.when(teamStatusService.getTeamStatusList(Mockito.any())).thenReturn(null);

      val result = underTest.getStimmzettelerfassungTeamStatusList(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();
    }
  }
}
