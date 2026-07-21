package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.TeamErfassungStatusModel;
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

  @Mock TeamErfassungStatusDTOMapper teamErfassungStatusDTOMapper;

  @InjectMocks private StimmzettelerfassungTeamStatusController underTest;

  @Nested
  class SaveTeamStimmzettelerfassungStatus {

    @Test
    void should_callServiceWithMappedRequest_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);
      val requestBody = Instancio.create(TeamErfassungStatusDTO.class);

      val mockedMappedStatus = Instancio.create(TeamErfassungStatusModel.class);
      Mockito.when(teamErfassungStatusDTOMapper.toModel(requestBody))
          .thenReturn(mockedMappedStatus);

      underTest.saveTeamStimmzettelerfassungStatus(wahlID, wahlbezirkID, teamID, requestBody);

      Mockito.verify(teamStatusService)
          .saveTeamStatus(
              new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID), mockedMappedStatus);
    }
  }

  @Nested
  class GetTeamStimmzettelerfassungStatus {

    @Test
    void should_returnOkWithResponse_when_serviceHasData() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val mockedServiceResponse = Instancio.create(TeamErfassungStatusModel.class);
      Mockito.when(
              teamStatusService.getTeamStatus(
                  new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID)))
          .thenReturn(Optional.of(mockedServiceResponse));

      val mockedMappeServiceResponse = Instancio.create(TeamErfassungStatusDTO.class);
      Mockito.when(teamErfassungStatusDTOMapper.toDTO(mockedServiceResponse))
          .thenReturn(mockedMappeServiceResponse);

      val result = underTest.getTeamStimmzettelerfassungStatus(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

      Assertions.assertThat(result.getBody()).isEqualTo(mockedMappeServiceResponse);
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

      val result = underTest.getTeamStimmzettelerfassungStatus(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();
    }
  }
}
