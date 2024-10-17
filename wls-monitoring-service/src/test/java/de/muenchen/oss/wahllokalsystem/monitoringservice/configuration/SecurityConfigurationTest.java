package de.muenchen.oss.wahllokalsystem.monitoringservice.configuration;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl.WaehleranzahlDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @MockBean
    WaehleranzahlService waehleranzahlService;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void accessSecuredResourceRootThenUnauthorized() throws Exception {
        api.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessSecuredResourceActuatorThenUnauthorized() throws Exception {
        api.perform(get("/actuator"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessUnsecuredResourceActuatorHealthThenOk() throws Exception {
        api.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceActuatorInfoThenOk() throws Exception {
        api.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceActuatorMetricsThenOk() throws Exception {
        api.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceV3ApiDocsThenOk() throws Exception {
        api.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceSwaggerUiThenOk() throws Exception {
        api.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Nested
    class Waehleranzahl {

        @Test
        @WithAnonymousUser
        void get_should_return401Unauthorized_when_UnauthorizedAnonymousUser() throws Exception {
            api.perform(get("/businessActions/wahlbeteiligung/wahlID/wahlbezirkID")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void get_should_return200OK_when_AuthorizedMockUser() throws Exception {
            api.perform(get("/businessActions/wahlbeteiligung/wahlID/wahlbezirkID")).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void post_should_return401Unauthorized_when_UnauthorizedAnonymousUser() throws Exception {
            api.perform(post("/businessActions/wahlbeteiligung/wahlID/wahlbezirkID").with(csrf())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void post_should_return200OK_when_AuthorizedMockUser() throws Exception {
            val requestBody = new WaehleranzahlDTO(null, null, null, null);
            val request = post("/businessActions/wahlbeteiligung/wahlID/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isOk());
        }

    }

}
