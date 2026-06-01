package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.configuration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung.BriefwahlvorbereitungDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit.EroeffnungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit.FortsetzungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit.UnterbrechungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisService;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = {TestConstants.SPRING_TEST_PROFILE})
public class SecurityConfigurationTest {

  @MockitoBean UrnenwahlvorbereitungService urnenwahlvorbereitungService;

  @MockitoBean WaehlerverzeichnisService waehlerverzeichnisService;

  @MockitoBean UrnenwahlSchliessungsUhrzeitService urnenwahlSchliessungsUhrzeitService;

  @MockitoBean UnterbrechungsUhrzeitService unterbrechungsUhrzeitService;

  @MockitoBean FortsetzungsUhrzeitService fortsetzungsUhrzeitService;

  @MockitoBean EroeffnungsUhrzeitService eroeffnungsUhrzeitService;

  @MockitoBean BriefwahlvorbereitungService briefwahlvorbereitungService;

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Test
  void should_returnUnauthorized_when_accessingRoot() throws Exception {
    mockMvc.perform(get("/")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnUnauthorized_when_accessingActuator() throws Exception {
    mockMvc.perform(get("/actuator")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnOk_when_accessingActuatorHealth() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingActuatorInfo() throws Exception {
    mockMvc.perform(get("/actuator/info")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingActuatorMetrics() throws Exception {
    mockMvc.perform(get("/actuator/metrics")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingApiDocs() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
  }

  @Test
  void should_returnOk_when_accessingSwaggerUi() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }

  @Nested
  class Urnenwahlvorbereitung {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/urnenwahlVorbereitung/wahlbezirkID");

      mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/urnenwahlVorbereitung/wahlbezirkID");

      mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/urnenwahlVorbereitung/wahlbezirkID")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}");

      mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/urnenwahlVorbereitung/wahlbezirkID")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}");

      mockMvc.perform(request).andExpect(status().isCreated());
    }
  }

  @Nested
  class Waehlerverzeichnis {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/waehlerverzeichnis/waehlerbezirkID/1");

      mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/waehlerverzeichnis/waehlerbezirkID/1");

      mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/waehlerverzeichnis/waehlerbezirkID/1")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}");

      mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/waehlerverzeichnis/waehlerbezirkID/1")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}");

      mockMvc.perform(request).andExpect(status().isCreated());
    }
  }

  @Nested
  class Urnenwahlschliessungsuhrzeit {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/urnenwahlSchliessungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/urnenwahlSchliessungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new UrnenwahlSchliessungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/urnenwahlSchliessungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPot() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new UrnenwahlSchliessungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/urnenwahlSchliessungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }
  }

  @Nested
  class Unterbrechungsuhrzeit {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/unterbrechungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/unterbrechungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new UnterbrechungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/unterbrechungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPot() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new UnterbrechungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/unterbrechungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }
  }

  @Nested
  class Fortsetzungsuhrzeit {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/fortsetzungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/fortsetzungsUhrzeit/wahlbezirkID"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new FortsetzungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/fortsetzungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPot() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new FortsetzungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/fortsetzungsUhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }
  }

  @Nested
  class Eroeffnungsuhrzeit {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/eroeffnungsuhrzeit/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/eroeffnungsuhrzeit/wahlbezirkID"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new EroeffnungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/eroeffnungsuhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPot() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new EroeffnungsUhrzeitDTO("wahlbezirkID", LocalDateTime.now()));
      mockMvc
          .perform(
              post("/businessActions/eroeffnungsuhrzeit/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }
  }

  @Nested
  class Briefwahlvorbereitung {
    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/briefwahlvorbereitung/wahlbezirkID"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      mockMvc
          .perform(get("/businessActions/briefwahlvorbereitung/wahlbezirkID"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new BriefwahlvorbereitungDTO("wahlbezirkID", Collections.emptyList()));
      mockMvc
          .perform(
              post("/businessActions/briefwahlvorbereitung/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPot() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new BriefwahlvorbereitungDTO("wahlbezirkID", Collections.emptyList()));
      mockMvc
          .perform(
              post("/businessActions/briefwahlvorbereitung/wahlbezirkID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }
  }
}
