package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, properties = "service.config.oauth2.logoutUri=http://test.local")
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class AuthServerControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetLogoutUrl {

        @WithMockUser(username = "authenticated user")
        @Test
        void should_returnLogoutUri_when_propertyIsSet() throws Exception {
            val request = MockMvcRequestBuilders.get("/authserver/logouturl");
            val result = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseAsDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ResolvedUrlDTO.class);

            Assertions.assertThat(responseAsDTO.url()).isEqualTo("http://test.local");
        }
    }
}
