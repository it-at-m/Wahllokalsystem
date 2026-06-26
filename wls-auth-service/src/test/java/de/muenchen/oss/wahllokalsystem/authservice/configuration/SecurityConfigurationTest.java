package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.rest.WahlbezirksartDTO;
import de.muenchen.oss.wahllokalsystem.authservice.rest.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.authservice.service.SessionService;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import java.time.LocalDate;
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
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
class SecurityConfigurationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean UserService userService;

  @MockitoBean SessionService sessionService;

  @Test
  void should_returnStatusUnauthorized_when_accessingSecuredResourceRoot() throws Exception {
    api.perform(get("/")).andExpect(status().isInternalServerError());
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
  class User {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnlockUserUnauthorized() throws Exception {
      api.perform(post("/user/username/unlock").with(csrf())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_denyAccess_when_accessingUnlockUserAuthorizedButWithoutRequiredAuthority()
        throws Exception {
      api.perform(post("/user/username/unlock").with(csrf()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN_ADMIN")
    void should_permitAccess_when_accessingUnlockUserAuthorized() throws Exception {
      api.perform(post("/user/username/unlock").with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUserUnauthorized() throws Exception {
      api.perform(get("/user")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingUserAuthorized() throws Exception {
      api.perform(get("/user")).andExpect(status().isOk());
    }
  }

  @Nested
  class Session {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingInvalidateSessionUnauthorized() throws Exception {
      api.perform(post("/oauthsessions/sessionID/invalidate").with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_denyAccess_when_accessingInvalidateSessionAuthorizedButWithoutRequiredAuthority()
        throws Exception {
      api.perform(post("/oauthsessions/sessionID/invalidate").with(csrf()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN_ADMIN")
    void should_permitAccess_when_accessingInvalidateSessionAuthorized() throws Exception {
      api.perform(post("/oauthsessions/sessionID/invalidate").with(csrf()))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingOauthSessionsUnauthorized() throws Exception {
      api.perform(get("/oauthsessions/")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_denyAccess_when_accessingOauthSessionsAuthorizedButWithoutRequiredAuthority()
        throws Exception {
      api.perform(get("/oauthsessions/")).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN_ADMIN")
    void should_permitAccess_when_accessingOauthSessionsAuthorized() throws Exception {
      api.perform(get("/oauthsessions/")).andExpect(status().isOk());
    }
  }

  @Nested
  class Wahllokalbenutzer {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingGenerateAndExportUnauthorized() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahllokalUserInfoDTO[] {
                new WahllokalUserInfoDTO(
                    "wahlbezirknummer", LocalDate.now(), "wahlbezirkID", WahlbezirksartDTO.UWB, "")
              });
      api.perform(
              post("/generateAndExportWahllokalbenutzer/wahltagID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingGenerateAndExportAuthorized() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(
              new WahllokalUserInfoDTO[] {
                new WahllokalUserInfoDTO(
                    "wahlbezirknummer", LocalDate.now(), "wahlbezirkID", WahlbezirksartDTO.UWB, "")
              });
      api.perform(
              post("/generateAndExportWahllokalbenutzer/wahltagID")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBodyAsString))
          .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingDeleteWahllokalbenutzerUnauthorized() throws Exception {
      api.perform(delete("/deleteWahllokalbenutzer/wahltagID").with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingDeleteWahllokalbenutzerAuthorized() throws Exception {
      api.perform(delete("/deleteWahllokalbenutzer/wahltagID").with(csrf()))
          .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingExportWahllokalbenutzerUnauthorized() throws Exception {
      api.perform(get("/exportWahllokalbenutzer/wahltagID")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingExportWahllokalbenutzerAuthorized() throws Exception {
      api.perform(get("/exportWahllokalbenutzer/wahltagID")).andExpect(status().isCreated());
    }
  }

  @Nested
  class Authserver {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingLogouturlUnauthorized() throws Exception {
      api.perform(get("/authserver/logouturl")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN_ADMIN")
    void should_permitAccess_when_accessingogouturlAuthorized() throws Exception {
      api.perform(get("/authserver/logouturl")).andExpect(status().isOk());
    }
  }

  @Nested
  class Roles {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_roleMappingsUnauthorized() throws Exception {
      api.perform(get("/roles/mappings")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    void should_permitAccess_when_roleMappingsAuthorized() throws Exception {
      api.perform(get("/roles/mappings")).andExpect(status().isOk());
    }
  }
}
