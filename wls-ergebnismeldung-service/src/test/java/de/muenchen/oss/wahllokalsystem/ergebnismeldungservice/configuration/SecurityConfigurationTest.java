package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung.BegruendungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.BezirkUndWahlIDStapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.ErgebnisseDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw.BedenklicherStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw.ValidityDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.MeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.StatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.ValidierungsstatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege.StimmzettelumschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine.WahlscheineDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class SecurityConfigurationTest {

  @MockitoBean StatusService statusService;

  @MockitoBean AusdruckService ausdruckService;

  @MockitoBean StimmabgabevermerkeService stimmabgabevermerkeService;

  @MockitoBean ErgebnisseService ergebnisseService;

  @MockitoBean BegruendungService begruendungService;

  @MockitoBean ErgebnismeldungService ergebnismeldungService;

  @MockitoBean WahlscheineService wahlscheineService;

  @MockitoBean StimmzettelumschlaegeService stimmzettelumschlaegeService;

  @MockitoBean AWerteService aWerteService;

  @MockitoBean MBWBedenklicheStimmzettelService mbwBedenklicheStimmzettelService;

  @MockitoBean StimmzettelService stimmzettelService;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc api;

  @Test
  void should_returnStatusUnauthorized_when_accessingSecuredResourceRoot() throws Exception {
    api.perform(get("/")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnStatusUnauthorized_when_accessingSecuredResourceActuator() throws Exception {
    api.perform(get("/actuator")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceActuatorHealth() throws Exception {
    api.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceActuatorInfo() throws Exception {
    api.perform(get("/actuator/info")).andExpect(status().isOk());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceActuatorMetrics() throws Exception {
    api.perform(get("/actuator/metrics")).andExpect(status().isOk());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceV3ApiDocs() throws Exception {
    api.perform(get("/v3/api-docs")).andExpect(status().isOk());
  }

  @Test
  void should_returnStatusOk_when_accessingUnsecuredResourceSwaggerUi() throws Exception {
    api.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }

  @Nested
  class Status {

    @Nested
    class GetStatus {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_callingAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/status/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_callingAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/status/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isNoContent());

        Mockito.verify(statusService).getStatus(notNull());
      }
    }

    @Nested
    class PostStatus {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_callingAnonymous() throws Exception {
        val requestBody =
            new StatusDTO(
                new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
        val request =
            MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_callingAuthenticated() throws Exception {
        val requestBody =
            new StatusDTO(
                new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
        val request =
            MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        api.perform(request).andExpect(status().isOk());

        Mockito.verify(statusService).setStatus(notNull(), notNull());
      }
    }
  }

  @Nested
  class AsyncProgress {

    @WithAnonymousUser
    @Test
    void should_returnUnauthorized_when_callingGetAnonymous() throws Exception {
      val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void should_returnOk_when_callingAuthenticated() throws Exception {
      val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");

      api.perform(request).andExpect(status().isOk());
    }
  }

  @Nested
  class Ausdruck {

    @Nested
    class GetAusdruck {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_callingAnonymous() throws Exception {
        val request =
            MockMvcRequestBuilders.get("/businessActions/ausdruck/wahlID/wahlbezirkID/V1/html");

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNotFound_when_callingAuthenticated() throws Exception {
        val request =
            MockMvcRequestBuilders.get("/businessActions/ausdruck/wahlID/wahlbezirkID/V1/html");

        api.perform(request).andExpect(status().isNotFound());

        Mockito.verify(ausdruckService).getAusdruck(notNull());
      }
    }

    @Nested
    class getAllAusdrucke {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_callingAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/ausdruck/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnOK_when_callingAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/ausdruck/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isOk());

        Mockito.verify(ausdruckService).getAllAusdrucke(notNull(), notNull());
      }
    }

    @Nested
    class PostAusdruck {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_callingAnonymous() throws Exception {
        val requestBody =
            new StatusDTO(
                new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
        val request =
            MockMvcRequestBuilders.post("/businessActions/ausdruck/wahlID/wahlbezirkID/V1/html")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_callingAuthenticated() throws Exception {
        val requestBody =
            new StatusDTO(
                new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
        val request =
            MockMvcRequestBuilders.post("/businessActions/ausdruck/wahlID/wahlbezirkID/V1/html")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody));

        api.perform(request).andExpect(status().isOk());

        Mockito.verify(ausdruckService).saveAusdruck(notNull());
      }
    }
  }

  @Nested
  class Stimmabgabevermerke {

    @WithAnonymousUser
    @Test
    void should_returnUnauthorized_when_callingGetAnonymous() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/stimmabgabevermerke/wbzID/wahlid/1");

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void should_returnNoContent_when_callingGetAuthenticated() throws Exception {
      val request =
          MockMvcRequestBuilders.get("/businessActions/stimmabgabevermerke/wbzID/wahlid/1");

      api.perform(request).andExpect(status().isNoContent());

      Mockito.verify(stimmabgabevermerkeService).getStimmabgabevermerke("wbzID", "wahlid", 1L);
    }

    @WithAnonymousUser
    @Test
    void should_returnUnauthorized_when_callingSetAnonymous() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/stimmabgabevermerke/wbzID/wahlid/1")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(
                  objectMapper.writeValueAsBytes(
                      new StimmabgabevermerkeDTO(
                          "wahlbezirkID",
                          "wahlID",
                          0L,
                          Collections.emptySet(),
                          Collections.emptySet())));

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void should_returnOk_when_callingSetAuthenticated() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/stimmabgabevermerke/wbzID/wahlid/1")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(
                  objectMapper.writeValueAsBytes(
                      new StimmabgabevermerkeDTO(
                          "wahlbezirkID",
                          "wahlID",
                          0L,
                          Collections.emptySet(),
                          Collections.emptySet())));

      api.perform(request).andExpect(status().isOk());

      Mockito.verify(stimmabgabevermerkeService).postStimmabgabevermerke(any());
    }
  }

  @Nested
  class Ergebnisse {

    @Nested
    class GetErgebnisse {

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request =
            MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A");

        api.perform(request).andExpect(status().isNoContent()).andReturn();

        Mockito.verify(ergebnisseService).getErgebnisse(notNull());
      }

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request =
            MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A");

        api.perform(request).andExpect(status().isUnauthorized()).andReturn();
      }
    }

    @Nested
    class PostErgebnisse {

      @WithMockUser
      @Test
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val ergebnis1 = new ErgebnisDTO(null, null, null, 1, null);
        val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
        newErgebnisDTOList.add(ergebnis1);

        val ergebnisse =
            new ErgebnisseDTO(
                new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.BTW_A),
                newErgebnisDTOList);
        val request =
            MockMvcRequestBuilders.post("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(ergebnisse));
        api.perform(request).andExpect(status().isOk()).andReturn();

        Mockito.verify(ergebnisseService).postErgebnisse(any(), any());
      }

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val ergebnis1 = new ErgebnisDTO(null, null, null, 1, null);
        val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
        newErgebnisDTOList.add(ergebnis1);

        val ergebnisse =
            new ErgebnisseDTO(
                new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.BTW_A),
                newErgebnisDTOList);
        val request =
            MockMvcRequestBuilders.post("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(ergebnisse));

        api.perform(request).andExpect(status().isUnauthorized()).andReturn();
      }
    }

    @Nested
    class GetAllErgebnisse {

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isNoContent()).andReturn();

        Mockito.verify(ergebnisseService).getAllErgebnisse(notNull(), notNull());
      }

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID");

        api.perform(request).andExpect(status().isUnauthorized()).andReturn();
      }
    }
  }

  @Nested
  class Begruendung {

    @Nested
    class GetBegruendung {

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request =
            MockMvcRequestBuilders.get(
                "/businessActions/begruendung/wahlID/wahlbezirkID/LTW_BZW_A");

        api.perform(request).andExpect(status().isNoContent()).andReturn();

        Mockito.verify(begruendungService).getBegruendung(notNull());
      }

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request =
            MockMvcRequestBuilders.get(
                "/businessActions/begruendung/wahlID/wahlbezirkID/LTW_BZW_A");

        api.perform(request).andExpect(status().isUnauthorized()).andReturn();
      }
    }

    @Nested
    class PostBegruendung {

      @WithMockUser
      @Test
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val begruendung =
            new BegruendungDTO(
                new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A),
                null,
                null,
                true,
                true);
        val request =
            MockMvcRequestBuilders.post(
                    "/businessActions/begruendung/wahlID/wahlbezirkID/LTW_BZW_A")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(begruendung));
        api.perform(request).andExpect(status().isOk()).andReturn();

        Mockito.verify(begruendungService).postBegruendung(any(), any());
      }

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val begruendung =
            new BegruendungDTO(
                new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.LTW_BZW_A),
                null,
                null,
                true,
                true);
        val request =
            MockMvcRequestBuilders.post(
                    "/businessActions/begruendung/wahlID/wahlbezirkID/LTW_BZW_A")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(begruendung));

        api.perform(request).andExpect(status().isUnauthorized()).andReturn();
      }
    }
  }

  @Nested
  class Ergebnismeldung {

    @WithMockUser
    @Test
    void should_returnOk_when_userIsAuthenticated() throws Exception {
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/sendErgebnismeldung/wahlID/wahlbezirkID/1/V1/hauptwahlbezirkID")
              .header("forceergebnismeldung", "true")
              .with(csrf());

      api.perform(request).andExpect(status().isOk());

      Mockito.verify(ergebnismeldungService).updateSendungszeiten(notNull());
    }

    @WithAnonymousUser
    @Test
    void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/sendErgebnismeldung/wahlID/wahlbezirkID/1/V1/hauptwahlbezirkID")
              .header("forceergebnismeldung", "true")
              .with(csrf());

      api.perform(request).andExpect(status().isUnauthorized());
    }
  }

  @Nested
  class Wahlscheine {

    private static final String URL = "/businessActions/wahlscheine/wahlID/wahlbezirkID";

    @Nested
    class GetWahlscheine {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @Test
      @WithMockUser
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isNoContent());
      }
    }

    @Nested
    class PostWahlscheine {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new WahlscheineDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 0L));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new WahlscheineDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 0L));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isOk());
      }
    }
  }

  @Nested
  class Stimmzettelumschlaege {

    private static final String URL = "/businessActions/stimmzettelumschlaege/wahlID/wahlbezirkID";

    @Nested
    class GetStimmzettelumschlaege {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @Test
      @WithMockUser
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isNoContent());
      }
    }

    @Nested
    class PostStimmzettelumschlaege {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new StimmzettelumschlaegeDTO(
                    new BezirkUndWahlID("wahlID", "wahlbezirkID"), LocalDateTime.now(), 0L, 0L));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new StimmzettelumschlaegeDTO(
                    new BezirkUndWahlID("wahlID", "wahlbezirkID"), LocalDateTime.now(), 0L, 0L));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isOk());
      }
    }
  }

  @Nested
  class AWerte {

    @Nested
    class GetAWerte {

      private static final String URL = "/businessActions/awerte/wahlbezirkID";

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @Test
      @WithMockUser
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);

        api.perform(request).andExpect(status().isOk());
      }
    }

    @Nested
    class PostInit {

      private static final String URL = "/businessActions/awerte/init";

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(new String[] {"wahlbezirkID1", "wahlbezirkID2"});
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnOk_when_userIsAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(new String[] {"wahlbezirkID1", "wahlbezirkID2"});
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);

        api.perform(request).andExpect(status().isOk());
      }
    }
  }

  @Nested
  class MBWBedenklicheStimmzettel {

    private static final String URL =
        "/mbw/wahl/wahlID/wahlbezirk/wahlbezirkID/bedenklicheStimmzettel";

    @Nested
    class GetBedenklicheStimmzettel {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);
        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL);
        api.perform(request).andExpect(status().isNoContent());
      }
    }

    @Nested
    class PostBedenklicheStimmzettel {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                List.of(
                    new BedenklicherStimmzettelDTO(0, Collections.emptySet(), ValidityDTO.VALID)));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);
        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnCreated_when_userIsAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                List.of(
                    new BedenklicherStimmzettelDTO(0, Collections.emptySet(), ValidityDTO.VALID)));
        val request =
            MockMvcRequestBuilders.post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);
        api.perform(request).andExpect(status().isCreated());
      }
    }
  }

  @Nested
  class Stimmzettel {

    private static final String URL_STIMMZETTEL =
        "/stimmzettelerfassung/wahl/wahlID/wahlbezirk/wahlbezirkID/team/teamID/stimmzettel";

    private static final String URL_ANZAHL_STIMMZETTEL =
        "/stimmzettelerfassung/wahl/wahlID/wahlbezirk/wahlbezirkID/anzahlStimmzettel";

    @Nested
    class PostStimmzettel {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(List.of(Instancio.create(StimmzettelOfTeamDTO.class)));
        val request =
            MockMvcRequestBuilders.post(URL_STIMMZETTEL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);
        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnCreated_when_userIsAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(List.of(Instancio.create(StimmzettelOfTeamDTO.class)));
        val request =
            MockMvcRequestBuilders.post(URL_STIMMZETTEL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyAsString);
        api.perform(request).andExpect(status().isCreated());
      }
    }

    @Nested
    class GetStimmzettel {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL_STIMMZETTEL);
        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL_STIMMZETTEL);
        api.perform(request).andExpect(status().isNoContent());
      }
    }

    @Nested
    class GetAnzahlStimmzettel {

      @WithAnonymousUser
      @Test
      void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
        val request = MockMvcRequestBuilders.get(URL_ANZAHL_STIMMZETTEL);
        api.perform(request).andExpect(status().isUnauthorized());
      }

      @WithMockUser
      @Test
      void should_returnNoContent_when_userIsAuthenticated() throws Exception {
        val request = MockMvcRequestBuilders.get(URL_ANZAHL_STIMMZETTEL);
        api.perform(request).andExpect(status().isOk());
      }
    }
  }
}
