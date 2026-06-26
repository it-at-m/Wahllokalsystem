package de.muenchen.oss.wahllokalsystem.authservice.rest.roles;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
      "service.config.user.authority.schriftfuehrerin="
          + RolesControllerIntegrationTest.ROLE_SCHRIFTFUEHERIN,
      "service.config.user.authority.admin=" + RolesControllerIntegrationTest.ROLE_ADMIN
    })
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
class RolesControllerIntegrationTest {

  public static final String ROLE_SCHRIFTFUEHERIN = "ROLE_SCHRIFTFUEHRERIN";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Nested
  class GetRoleMappings {

    @Test
    void should_returnRoleMappings_when_called() throws Exception {
      val request = MockMvcRequestBuilders.get("/roles/mappings").with(jwt());

      val performedRequest = api.perform(request).andExpect(status().isOk()).andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              performedRequest.getResponse().getContentAsString(), RoleMappingsDTO.class);

      val expectedResponseBody = new RoleMappingsDTO(ROLE_SCHRIFTFUEHERIN, ROLE_ADMIN);
      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
    }
  }
}
