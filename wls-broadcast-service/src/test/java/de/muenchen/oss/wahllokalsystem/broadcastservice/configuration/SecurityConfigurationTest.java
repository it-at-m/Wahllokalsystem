package de.muenchen.oss.wahllokalsystem.broadcastservice.configuration;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastService;
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

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class SecurityConfigurationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean BroadcastService broadcastService;

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
  class Broadcast {

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedPostMessageRead() throws Exception {
      val request = post("/businessActions/messageRead/nachrichID").with(csrf());

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedPostMessageRead() throws Exception {
      val request = post("/businessActions/messageRead/nachrichID").with(csrf());

      api.perform(request).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedPostMessage() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(new BroadcastMessageDTO(Collections.emptyList(), ""));
      val request =
          post("/businessActions/broadcast")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBodyAsString);
      ;

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedPostMessage() throws Exception {
      val requestBodyAsString =
          objectMapper.writeValueAsString(new BroadcastMessageDTO(Collections.emptyList(), ""));
      val request =
          post("/businessActions/broadcast")
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBodyAsString);

      api.perform(request).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void should_denyAccess_when_accessingUnauthorizedGetOldestMessage() throws Exception {
      val request = get("/businessActions/getMessage/wahlbezirkID");

      api.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void should_permitAccess_when_accessingAuthorizedGetOldestMessage() throws Exception {
      val request = get("/businessActions/getMessage/wahlbezirkID");

      api.perform(request).andExpect(status().isOk());
    }
  }
}
