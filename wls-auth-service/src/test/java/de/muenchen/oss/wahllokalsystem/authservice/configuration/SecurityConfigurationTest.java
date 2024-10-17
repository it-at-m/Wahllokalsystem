package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @Autowired
    MockMvc api;

    @Test
    void accessSecuredResourceRootThenInternalServerError() throws Exception {
        api.perform(get("/"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void accessSecuredResourceActuatorThenFound() throws Exception {
        api.perform(get("/actuator"))
                .andExpect(status().isFound());
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

}
