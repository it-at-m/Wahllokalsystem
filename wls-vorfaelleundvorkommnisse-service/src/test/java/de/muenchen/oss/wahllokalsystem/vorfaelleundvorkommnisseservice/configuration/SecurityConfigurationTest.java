package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.configuration;

import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisartDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis.EreignisService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.filter.UserMdcFilter;
import jakarta.servlet.Filter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
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

  @Autowired FilterChainProxy filterChainProxy;

  @MockitoBean EreignisService ereignisService;

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
    api.perform(get("/webjars/swagger-ui/index.html")).andExpect(status().isOk());
  }

  @Test
  void should_registerUserMdcFilterAfterBearerTokenFilter_when_bothFiltersRegistered() {
    List<SecurityFilterChain> chains = filterChainProxy.getFilterChains();

    boolean foundChainWithBothFilters = false;
    for (SecurityFilterChain chain : chains) {
      List<Filter> filters = chain.getFilters();
      int bearerIndex = -1;
      int userMdcIndex = -1;
      for (int i = 0; i < filters.size(); i++) {
        Filter filter = filters.get(i);
        if (filter instanceof BearerTokenAuthenticationFilter) {
          bearerIndex = i;
        }
        if (filter instanceof UserMdcFilter) {
          userMdcIndex = i;
        }
      }
      if (bearerIndex != -1 && userMdcIndex != -1) {
        Assertions.assertThat(userMdcIndex)
                .as("UserMdcFilter must be registered after BearerTokenAuthenticationFilter")
                .isGreaterThan(bearerIndex);
        foundChainWithBothFilters = true;
        break;
      }
    }
    if (!foundChainWithBothFilters) {
      Assertions.fail(
              "No security filter chain contains both BearerTokenAuthenticationFilter and UserMdcFilter");
    }
  }

  @Nested
  class Ereignis {

    private static final String URL = "/businessActions/ereignisse/wahlbezirkID";

    @Nested
    class GetEreignis {

      @Test
      @WithAnonymousUser
      void should_denyAccess_when_accessingAnonymous() throws Exception {
        api.perform(get(URL)).andExpect(status().isUnauthorized());
      }

      @Test
      @WithMockUser
      void should_permAccess_when_accessingAuthenticated() throws Exception {
        api.perform(get(URL)).andExpect(status().isNoContent());
      }
    }

    @Nested
    class PostErgeignis {

      @Test
      @WithAnonymousUser
      void should_denyAccess_when_accessingAnonymous() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new EreignisDTO("beschreibung", LocalDateTime.now(), EreignisartDTO.VORFALL));
        api.perform(
                post(URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyAsString))
            .andExpect(status().isUnauthorized());
      }

      @Test
      @WithMockUser
      void should_permAccess_when_accessingAuthenticated() throws Exception {
        val requestBodyAsString =
            objectMapper.writeValueAsString(
                new EreignisDTO("beschreibung", LocalDateTime.now(), EreignisartDTO.VORFALL));
        api.perform(
                post(URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyAsString))
            .andExpect(status().isOk());
      }
    }
  }
}
