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

  @InjectMocks private StimmzettelerfassungTeamStatusController underTest;

  @Nested
  class SaveStimmzettelerfassungTeamStatus {

    @Test
    void should_callServiceWithMappedRequest_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);
      val requestBody = Instancio.create(ErfassungTeamStatusDTO.class);

      val mockedMappedStatus = Instancio.create(ErfassungTeamStatusModel.class);
      Mockito.when(erfassungTeamStatusDTOMapper.toModel(requestBody))
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

      val result = underTest.getStimmzettelerfassungTeamStatus(wahlID, wahlbezirkID, teamID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      Assertions.assertThat(result.getBody()).isNull();
    }
  }
}
