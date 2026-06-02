package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung.WahlbeteiligungService;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenService;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand.WahllokalZustandService;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag.WahlvorschlagService;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

  @MockitoBean WahlvorstandService wahlvorstandService;

  @MockitoBean WahllokalZustandService wahllokalZustandService;

  @MockitoBean WahlbeteiligungService wahlbeteiligungService;

  @MockitoBean ErgebnismeldungService ergebnismeldungService;

  @MockitoBean WahldatenService wahldatenService;

  @MockitoBean WahlvorschlagService wahlvorschlagService;

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
  class Wahlvorstand {
    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
      api.perform(get("/wahlvorstaende?wahlbezirkID=wbzID")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
      api.perform(get("/wahlvorstaende?wahlbezirkID=wbzID")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPut() throws Exception {
      val wahlvorstandAktualisierung =
          new WahlvorstandsaktualisierungDTO(
              "wbzID",
              Set.of(new WahlvorstandsmitgliedAktualisierungDTO("id", true)),
              LocalDateTime.now());

      api.perform(
              put("/wahlvorstaende/anwesenheit")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(wahlvorstandAktualisierung)))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPut() throws Exception {
      val wahlvorstandAktualisierung =
          new WahlvorstandsaktualisierungDTO(
              "wbzID",
              Set.of(new WahlvorstandsmitgliedAktualisierungDTO("id", true)),
              LocalDateTime.now());

      api.perform(
              put("/wahlvorstaende/anwesenheit")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(wahlvorstandAktualisierung)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Wahllokalzustand {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahllokalZustandDTO(
                  "wahlbezirkID",
                  LocalDateTime.now(),
                  LocalDateTime.now(),
                  Collections.emptySet()));
      api.perform(
              post("/wahllokalzustand")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahllokalZustandDTO(
                  "wahlbezirkID",
                  LocalDateTime.now(),
                  LocalDateTime.now(),
                  Collections.emptySet()));
      api.perform(
              post("/wahllokalzustand")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Wahlbeteiligung {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahlbeteiligungsMeldungDTO("wahlID", "wahlbezirkID", 0, LocalDateTime.now()));
      api.perform(
              post("/wahlbeteiligung")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahlbeteiligungsMeldungDTO("wahlID", "wahlbezirkID", 0, LocalDateTime.now()));
      api.perform(
              post("/wahlbeteiligung")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Ergebnismeldung {
    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new ErgebnismeldungDTO(
                  "wahlbezirkID",
                  "wahlID",
                  MeldungsartDTO.NIEDERSCHRIFT,
                  null,
                  null,
                  null,
                  Collections.emptySet(),
                  0l,
                  Collections.emptySet(),
                  WahlartDTO.BEB));
      api.perform(
              post("/ergebnismeldung")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedViaPost() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new ErgebnismeldungDTO(
                  "wahlbezirkID",
                  "wahlID",
                  MeldungsartDTO.NIEDERSCHRIFT,
                  null,
                  null,
                  null,
                  Collections.emptySet(),
                  0l,
                  Collections.emptySet(),
                  WahlartDTO.BEB));
      api.perform(
              post("/ergebnismeldung")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class Wahldaten {

    private static final String WAHLTAGE_URL = "/wahldaten/wahltage?includingSince=2026-01-01";
    private static final String WAHLEN_URL = "/wahldaten/wahlen?forDate=2026-01-01&withNummer=1";
    private static final String WAHLBERECHTIGTE_URL =
        "/wahldaten/wahlbezirke/wahlbezirkID/wahlberechtigte";
    private static final String WAHLBEZIRK_URL =
        "/wahldaten/wahlbezirk?forDate=2026-01-01&withNummer=1";
    private static final String BASISDATEN_URL =
        "/wahldaten/basisdaten?forDate=2026-01-01&withNummer=1";

    @ParameterizedTest(name = "{arguments}")
    @ValueSource(
        strings = {WAHLTAGE_URL, WAHLEN_URL, WAHLBERECHTIGTE_URL, WAHLBEZIRK_URL, BASISDATEN_URL})
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizeViaGet(final String url) throws Exception {
      api.perform(get(url)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "{arguments}")
    @ValueSource(
        strings = {WAHLTAGE_URL, WAHLEN_URL, WAHLBERECHTIGTE_URL, WAHLBEZIRK_URL, BASISDATEN_URL})
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedWahltageViaGet(final String url)
        throws Exception {
      api.perform(get(url)).andExpect(status().isOk());
    }
  }

  @Nested
  class Wahvorschlag {

    private static final String WAHLVORSCHLAEGELISTE_URL =
        "/vorschlaege/wahl/wahlID/liste?forDate=2026-01-01";
    private static final String WAHLVORSCHLAEGE_URL = "/vorschlaege/wahl/wahlID/wahlbezirkID";
    private static final String REFERENDUMVORLAGEN_URL =
        "/vorschlaege/referendum/wahlID/wahlbezirkID";

    @ParameterizedTest(name = "{arguments}")
    @ValueSource(strings = {WAHLVORSCHLAEGELISTE_URL, WAHLVORSCHLAEGE_URL, REFERENDUMVORLAGEN_URL})
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizeViaGet(final String url) throws Exception {
      api.perform(get(url)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "{arguments}")
    @ValueSource(strings = {WAHLVORSCHLAEGELISTE_URL, WAHLVORSCHLAEGE_URL, REFERENDUMVORLAGEN_URL})
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedWahltageViaGet(final String url)
        throws Exception {
      api.perform(get(url)).andExpect(status().isOk());
    }
  }
}
