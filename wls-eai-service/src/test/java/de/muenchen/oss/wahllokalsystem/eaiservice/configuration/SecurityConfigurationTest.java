package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandService;
import java.time.LocalDateTime;
import java.util.Set;
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

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WahlvorstandService wahlvorstandService;

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
    class Wahlvorstand {
        @Test
        @WithAnonymousUser
        void accessGetWahlvorstandForWahlbezirkIDUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/wahlvorstaende?wahlbezirkID=wbzID")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetWahlvorstandForWahlbezirkIDAuthorizedThenOk() throws Exception {
            api.perform(get("/wahlvorstaende?wahlbezirkID=wbzID")).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessSaveAnwesenheitUnauthorizedThenUnauthorized() throws Exception {
            val wahlvorstandAktualisierung = new WahlvorstandsaktualisierungDTO("wbzID", Set.of(new WahlvorstandsmitgliedAktualisierungDTO("id", true)),
                    LocalDateTime.now());

            api.perform(post("/wahlvorstaende").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wahlvorstandAktualisierung))).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessSaveAnwesenheitAuthorizedThenCreated() throws Exception {
            val wahlvorstandAktualisierung = new WahlvorstandsaktualisierungDTO("wbzID", Set.of(new WahlvorstandsmitgliedAktualisierungDTO("id", true)),
                    LocalDateTime.now());

            api.perform(post("/wahlvorstaende").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wahlvorstandAktualisierung))).andExpect(status().isOk());
        }
    }

}
