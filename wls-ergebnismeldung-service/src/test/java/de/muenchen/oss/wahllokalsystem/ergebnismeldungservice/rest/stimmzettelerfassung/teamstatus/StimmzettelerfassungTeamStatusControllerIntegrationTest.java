package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class StimmzettelerfassungTeamStatusControllerIntegrationTest {

  @Autowired StimmzettelerfassungTeamStatusRepository repository;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    repository.deleteAll();
  }

  @Nested
  class SaveTeamStimmzettelerfassungStatus {

    @Test
    void should_persistData_when_dataIsSent() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val id = new TeamBezirkUndWahlID(wahlID, wahlbezirkID, teamID);
      val expectedEntity =
          new StimmzettelerfassungTeamStatus(id, TeamErfassungStatus.IN_BEARBEITUNG);

      val requestBody =
          new StimmzettelerfassungTeamStatusDTO(TeamErfassungStatusDTO.IN_BEARBEITUNG);

      mockMvc
          .perform(
              createPostRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID, requestBody))
          .andExpect(status().isCreated());

      val persistedEntity = repository.findById(id).get();

      Assertions.assertThat(persistedEntity).isEqualTo(expectedEntity);
    }

    @Test
    void should_replaceExistingData_when_dataIsSent() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val id = new TeamBezirkUndWahlID(wahlID, wahlbezirkID, teamID);
      val entityToReplace = new StimmzettelerfassungTeamStatus(id, TeamErfassungStatus.REGISTRIERT);

      repository.save(entityToReplace);

      val expectedEntity =
          new StimmzettelerfassungTeamStatus(id, TeamErfassungStatus.IN_BEARBEITUNG);

      val requestBody =
          new StimmzettelerfassungTeamStatusDTO(TeamErfassungStatusDTO.IN_BEARBEITUNG);

      mockMvc
          .perform(
              createPostRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID, requestBody))
          .andExpect(status().isCreated());

      val persistedEntity = repository.findById(id).get();

      Assertions.assertThat(persistedEntity).isEqualTo(expectedEntity);

      Assertions.assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      mockMvc
          .perform(createPostRequest(" ", " ", " ", " ", " ", null))
          .andExpect(status().isBadRequest());

      Assertions.assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void should_returnForbidden_when_userHasWrongClaims() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val requestBody = new StimmzettelerfassungTeamStatusDTO(TeamErfassungStatusDTO.REGISTRIERT);

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

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String teamID,
        final String claimWahlbezirkID,
        final String claimTeamID,
        final StimmzettelerfassungTeamStatusDTO requestBody)
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
                      new SimpleGrantedAuthority(
                          Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("teamID", claimTeamID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody == null ? "" : objectMapper.writeValueAsString(requestBody));
    }
  }

  @Nested
  class GetTeamStimmzettelerfassungStatus {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val teamID = "teamID";
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val status = TeamErfassungStatus.UNTERBROCHEN;

      val entityToFind =
          new StimmzettelerfassungTeamStatus(
              new TeamBezirkUndWahlID(wahlID, wahlbezirkID, teamID), status);

      repository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, teamID, wahlbezirkID, teamID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();

      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getContentAsString(), StimmzettelerfassungTeamStatusDTO.class);

      val expectedResult =
          new StimmzettelerfassungTeamStatusDTO(TeamErfassungStatusDTO.UNTERBROCHEN);

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
                          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("teamID", claimTeamID)));
    }
  }
}
