package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.instancio.Select.field;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.ErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.ErfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Arrays;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class StimmzettelerfassungTeamStatusControllerIntegrationTest {

  @MockitoSpyBean StimmzettelerfassungTeamStatusRepository teamstatusRepository;

  @MockitoSpyBean StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;

  @Autowired ObjectMapper objectMapper;

  @Autowired ServiceIDFormatter serviceIDFormatter;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    teamstatusRepository.deleteAll();
    stimmzettelerfassungStatusRepository.deleteAll();
  }

  @Nested
  class SaveStimmzettelerfassungTeamStatus {

    @Test
    void should_persistData_when_dataIsSent() throws Exception {
      val id = Instancio.create(TeamBezirkUndWahlID.class);

      val requestBody = Instancio.create(ErfassungTeamStatusDTO.class);

      mockMvc
          .perform(
              createPostRequest(
                  id.getWahlID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  requestBody))
          .andExpect(status().isCreated());

      val persistedTeamStatusEntity = teamstatusRepository.findById(id).get();

      val expectedEntity =
          Instancio.of(StimmzettelerfassungTeamStatus.class)
              .set(field(StimmzettelerfassungTeamStatus::getId), id)
              .set(
                  field(StimmzettelerfassungTeamStatus::getStatus),
                  ErfassungTeamStatus.valueOf(requestBody.name()))
              .create();
      Assertions.assertThat(persistedTeamStatusEntity).isEqualTo(expectedEntity);
    }

    @Test
    void should_persistDataAndSetStimmzettelErfassungStatus_when_teamStatusIsInBearbeitung()
        throws Exception {
      val id = Instancio.create(TeamBezirkUndWahlID.class);

      val requestBody = ErfassungTeamStatusDTO.IN_BEARBEITUNG;
      Assertions.assertThat(
              stimmzettelerfassungStatusRepository.existsById(
                  new BezirkUndWahlID(id.getWahlID(), id.getWahlbezirkID())))
          .isFalse();
      mockMvc
          .perform(
              createPostRequest(
                  id.getWahlID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  requestBody))
          .andExpect(status().isCreated());

      val persistedTeamStatusEntity = teamstatusRepository.findById(id).get();

      val expectedEntity =
          Instancio.of(StimmzettelerfassungTeamStatus.class)
              .set(field(StimmzettelerfassungTeamStatus::getId), id)
              .set(
                  field(StimmzettelerfassungTeamStatus::getStatus),
                  ErfassungTeamStatus.valueOf(requestBody.name()))
              .create();
      Assertions.assertThat(persistedTeamStatusEntity).isEqualTo(expectedEntity);

      val persistedStimmzettelErfassungStatusEntity =
          stimmzettelerfassungStatusRepository
              .findById(new BezirkUndWahlID(id.getWahlID(), id.getWahlbezirkID()))
              .get();
      Assertions.assertThat(persistedStimmzettelErfassungStatusEntity.getStatus())
          .isEqualTo(ErfassungStatus.STE_BEARBEITUNG);
    }

    @Test
    void should_replaceExistingData_when_dataIsSent() throws Exception {
      val id = Instancio.create(TeamBezirkUndWahlID.class);
      val entityToReplace = new StimmzettelerfassungTeamStatus(id, ErfassungTeamStatus.REGISTRIERT);

      teamstatusRepository.save(entityToReplace);

      val requestBody = ErfassungTeamStatusDTO.IN_BEARBEITUNG;

      mockMvc
          .perform(
              createPostRequest(
                  id.getWahlID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  id.getWahlbezirkID(),
                  id.getTeamID(),
                  requestBody))
          .andExpect(status().isCreated());

      val persistedEntity = teamstatusRepository.findById(id).get();

      val expectedEntity =
          new StimmzettelerfassungTeamStatus(id, ErfassungTeamStatus.IN_BEARBEITUNG);
      Assertions.assertThat(persistedEntity).isEqualTo(expectedEntity);

      Assertions.assertThat(teamstatusRepository.count()).isEqualTo(1);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      mockMvc
          .perform(createPostRequest(" ", " ", " ", " ", " ", null))
          .andExpect(status().isBadRequest());

      Assertions.assertThat(teamstatusRepository.count()).isEqualTo(0);
    }

    @Test
    void should_returnForbidden_when_userHasWrongClaims() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val requestBody = ErfassungTeamStatusDTO.REGISTRIERT;

      // wrong wahlbezirk claim
      mockMvc
          .perform(
              createPostRequest(
                  wahlID, wahlbezirkID, teamID, wahlbezirkID + "x", teamID, requestBody))
          .andExpect(status().isForbidden());

      // wrong team claim
      mockMvc
          .perform(
              createPostRequest(
                  wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID + "x", requestBody))
          .andExpect(status().isForbidden());
    }

    @Test
    void should_notSaveStimmzettelerfassungStatus_when_savingTeamstatusFailed() throws Exception {
      val id = Instancio.create(TeamBezirkUndWahlID.class);

      val requestBody = ErfassungTeamStatusDTO.IN_BEARBEITUNG;

      Mockito.doThrow(new RuntimeException("saving teamstatus failed"))
          .when(teamstatusRepository)
          .save(Mockito.any());

      val response =
          mockMvc
              .perform(
                  createPostRequest(
                      id.getWahlID(),
                      id.getWahlbezirkID(),
                      id.getTeamID(),
                      id.getWahlbezirkID(),
                      id.getTeamID(),
                      requestBody))
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

      val persistedTeamStatusEntity = teamstatusRepository.findById(id);
      val persistedWorkflowStatusEntity =
          stimmzettelerfassungStatusRepository.findById(
              new BezirkUndWahlID(id.getWahlID(), id.getWahlbezirkID()));
      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              "999",
              serviceIDFormatter.getId(),
              "Ursache: class java.lang.RuntimeException, Nachricht: saving teamstatus failed");

      Assertions.assertThat(persistedTeamStatusEntity).isEmpty();
      Assertions.assertThat(persistedWorkflowStatusEntity).isEmpty();
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_notSaveTeamstatus_when_savingStimmzettelerfassungStatusFailed() throws Exception {
      val id = Instancio.create(TeamBezirkUndWahlID.class);

      val requestBody = ErfassungTeamStatusDTO.IN_BEARBEITUNG;

      Mockito.doThrow(new RuntimeException("saving stimmzettelerfassungStatus failed"))
          .when(stimmzettelerfassungStatusRepository)
          .save(Mockito.any());

      val response =
          mockMvc
              .perform(
                  createPostRequest(
                      id.getWahlID(),
                      id.getWahlbezirkID(),
                      id.getTeamID(),
                      id.getWahlbezirkID(),
                      id.getTeamID(),
                      requestBody))
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

      val persistedTeamStatusEntity = teamstatusRepository.findById(id);
      val persistedWorkflowStatusEntity =
          stimmzettelerfassungStatusRepository.findById(
              new BezirkUndWahlID(id.getWahlID(), id.getWahlbezirkID()));
      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              "999",
              serviceIDFormatter.getId(),
              "Ursache: class java.lang.RuntimeException, Nachricht: saving stimmzettelerfassungStatus failed");

      Assertions.assertThat(persistedTeamStatusEntity).isEmpty();
      Assertions.assertThat(persistedWorkflowStatusEntity).isEmpty();
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String teamID,
        final String claimWahlbezirkID,
        final String claimTeamID,
        final ErfassungTeamStatusDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/stimmzettelerfassung/wahl/"
                  + wahlID
                  + "/wahlbezirk/"
                  + wahlbezirkID
                  + "/team/"
                  + teamID
                  + "/status")
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      Arrays.stream(Authorities.ALL_AUTHORITIES_SAVE_TEAMSTATUS)
                          .map(SimpleGrantedAuthority::new)
                          .toArray(SimpleGrantedAuthority[]::new))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("teamID", claimTeamID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody == null ? "" : objectMapper.writeValueAsString(requestBody));
    }
  }

  @Nested
  class GetStimmzettelerfassungTeamStatus {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val status = ErfassungTeamStatus.UNTERBROCHEN;

      val entityToFind =
          new StimmzettelerfassungTeamStatus(
              new TeamBezirkUndWahlID(wahlID, wahlbezirkID, teamID), status);

      teamstatusRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();

      val responseBodyAsDTO =
          objectMapper.readValue(response.getContentAsString(), ErfassungTeamStatusDTO.class);

      val expectedResult = ErfassungTeamStatusDTO.UNTERBROCHEN;

      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResult);
    }

    @Test
    void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID))
          .andExpect(status().isNoContent());
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      mockMvc.perform(createGetRequest(" ", " ", " ", " ", " ")).andExpect(status().isBadRequest());
    }

    @Test
    void should_returnForbidden_when_userHasWrongClaims() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      // wrong wahlbezirk
      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID + "x", teamID))
          .andExpect(status().isForbidden());

      // wrong team
      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID + "x"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String teamID,
        final String claimWahlbezirkID,
        final String claimTeamID) {
      return MockMvcRequestBuilders.get(
              "/stimmzettelerfassung/wahl/"
                  + wahlID
                  + "/wahlbezirk/"
                  + wahlbezirkID
                  + "/team/"
                  + teamID
                  + "/status")
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGTEAMSTATUS))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("teamID", claimTeamID)));
    }
  }
}
