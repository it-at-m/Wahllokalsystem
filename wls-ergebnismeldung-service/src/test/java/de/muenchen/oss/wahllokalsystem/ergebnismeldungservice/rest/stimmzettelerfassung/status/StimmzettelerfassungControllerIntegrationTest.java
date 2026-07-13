package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.status;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.ErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class StimmzettelerfassungControllerIntegrationTest {

  @Autowired StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    stimmzettelerfassungStatusRepository.deleteAll();
  }

  @Nested
  class SaveStimmzettelerfassungStatus {
    @Test
    void should_persistData_when_dataIsSent() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val id = new BezirkUndWahlID(wahlID, wahlbezirkID);
      val status = ErfassungStatus.STE_ABGESCHLOSSEN;
      val expectedEntity = new StimmzettelerfassungStatus(id, status);

      val requestBody = new StimmzettelerfassungStatusDTO(ErfassungStatusDTO.STE_ABGESCHLOSSEN);

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isCreated());

      val persistedEntity = stimmzettelerfassungStatusRepository.findById(id).get();

      Assertions.assertThat(persistedEntity).isEqualTo(expectedEntity);
    }

    @Test
    void should_replaceExistingData_when_dataIsSent() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val id = new BezirkUndWahlID(wahlID, wahlbezirkID);
      val status = ErfassungStatus.STE_BEARBEITUNG;
      val entityToReplace = new StimmzettelerfassungStatus(id, status);

      stimmzettelerfassungStatusRepository.save(entityToReplace);

      val expectedEntity = new StimmzettelerfassungStatus(id, ErfassungStatus.STE_ABGESCHLOSSEN);

      val requestBody = new StimmzettelerfassungStatusDTO(ErfassungStatusDTO.STE_ABGESCHLOSSEN);

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isCreated());

      val persistedEntity = stimmzettelerfassungStatusRepository.findById(id).get();

      Assertions.assertThat(persistedEntity).isEqualTo(expectedEntity);

      Assertions.assertThat(stimmzettelerfassungStatusRepository.count()).isEqualTo(1);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      mockMvc
          .perform(createPostRequest("wahlID", " ", "wahlbezirkID", null))
          .andExpect(status().isBadRequest());

      Assertions.assertThat(stimmzettelerfassungStatusRepository.count()).isEqualTo(0);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val requestBody = new StimmzettelerfassungStatusDTO(ErfassungStatusDTO.STE_ABGESCHLOSSEN);
      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", requestBody))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final StimmzettelerfassungStatusDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/businessActions/stimmzettelerfassungsWorkflow/wahl/"
                  + wahlID
                  + "/wahlbezirk/"
                  + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }

  @Nested
  class GetStimmzettelerfassungStatus {
    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val status = ErfassungStatus.STE_ABGESCHLOSSEN;

      val entityToFind =
          new StimmzettelerfassungStatus(new BezirkUndWahlID(wahlID, wahlbezirkID), status);

      stimmzettelerfassungStatusRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getContentAsString(), StimmzettelerfassungStatusDTO.class);

      val expectedResult = new StimmzettelerfassungStatusDTO(ErfassungStatusDTO.STE_ABGESCHLOSSEN);

      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResult);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      val wahlbezirkID = "  ";
      val wahlID = " ";

      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
          .andExpect(status().isBadRequest());
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(
              "/businessActions/stimmzettelerfassungsWorkflow/wahl/"
                  + wahlID
                  + "/wahlbezirk/"
                  + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }
}
