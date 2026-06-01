package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.kopfdaten.KopfdatenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.ungueltigewahlscheine.UngueltigeWahlscheineService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlbezirke.WahlbezirkeService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltermindaten.WahltermindatenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlag.WahlvorschlaegeService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class SecurityConfigurationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean WahlvorschlaegeService wahlvorschlaegeService;

  @MockitoBean WahltageService wahltageService;

  @MockitoBean HandbuchService handbuchService;

  @MockitoBean UngueltigeWahlscheineService ungueltigeWahlscheineService;

  @MockitoBean ReferendumvorlagenService referendumvorlagenService;

  @MockitoBean WahltermindatenService wahltermindatenService;

  @MockitoBean WahlenService wahlenService;

  @MockitoBean WahlbezirkeService wahlbezirkeService;

  @MockitoBean KopfdatenService kopfdatenService;

  @Test
  void should_returnUnauthorized_when_accessingRoot() throws Exception {
    api.perform(get("/")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnUnauthorized_when_accessingActuator() throws Exception {
    api.perform(get("/actuator")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnOk_when_accessingActuatorHealth() throws Exception {
    api.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingActuatorInfo() throws Exception {
    api.perform(get("/actuator/info")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingActuatorMetrics() throws Exception {
    api.perform(get("/actuator/metrics")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingApiDocs() throws Exception {
    api.perform(get("/v3/api-docs")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingSwaggerUi() throws Exception {
    api.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }

  @Nested
  class Kopfdaten {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/kopfdaten/wahlID/wahbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/kopfdaten/wahlID/wahbezirkID")).andExpect(status().isOk());
    }
  }

  @Nested
  class Wahlbezirke {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlbezirke/wahltagID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlbezirke/wahltagID")).andExpect(status().isOk());
    }
  }

  @Nested
  class Wahlvorschlaege {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlvorschlaege/wahlID/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlvorschlaege/wahlID/wahlbezirkID"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Wahltage {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahltage")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahltage")).andExpect(status().isOk());
    }
  }

  @Nested
  class Handbuch {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/handbuch/wahlID/UWB")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/handbuch/wahlID/UWB")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      api.perform(
              multipart("/businessActions/handbuch/wahlID/UWB")
                  .file("manual", "content".getBytes())
                  .with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      api.perform(
              multipart("/businessActions/handbuch/wahlID/UWB")
                  .file("manual", "content".getBytes())
                  .with(csrf()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UngueltigeWahlscheine {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/ungueltigews/wahlID/UWB"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/ungueltigews/wahlID/UWB")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      api.perform(
              multipart("/businessActions/ungueltigews/wahlID/UWB")
                  .file("manual", "content".getBytes())
                  .with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      api.perform(
              multipart("/businessActions/ungueltigews/wahlID/UWB")
                  .file("manual", "content".getBytes())
                  .with(csrf()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Referendumvorlagen {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/referendumvorlagen/wahlID/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/referendumvorlagen/wahlID/wahlbezirkID"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Wahlen {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlen/wahltagID")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/businessActions/wahlen/wahltagID")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBody = new WahlDTO[0];
      api.perform(
              post("/businessActions/wahlen/wahltagID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestBody)))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedPost() throws Exception {
      val requestBody = new WahlDTO[0];
      api.perform(
              post("/businessActions/wahlen/wahltagID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(requestBody)))
          .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingResetWahlenUnauthorizedViaPost() throws Exception {
      api.perform(post("/businessActions/resetWahlen").with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingResetWahlenAuthorizedPost() throws Exception {
      api.perform(post("/businessActions/resetWahlen").with(csrf())).andExpect(status().isOk());
    }
  }

  @Nested
  class Wahltermindaten {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPut() throws Exception {
      api.perform(put("/businessActions/wahltermindaten/wahlID").with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPut() throws Exception {
      api.perform(put("/businessActions/wahltermindaten/wahlID").with(csrf()))
          .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaDelete() throws Exception {
      api.perform(delete("/businessActions/wahltermindaten/wahlID").with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedDelete() throws Exception {
      api.perform(delete("/businessActions/wahltermindaten/wahlID").with(csrf()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AsyncProgress {
    @Test
    @WithAnonymousUser
    void should_denyAccess_when_requestWithAnonymousUser() throws Exception {
      api.perform(get("/businessActions/asyncProgress")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_requestWithAuthorizedUser() throws Exception {
      api.perform(get("/businessActions/asyncProgress")).andExpect(status().isOk());
    }
  }
}
