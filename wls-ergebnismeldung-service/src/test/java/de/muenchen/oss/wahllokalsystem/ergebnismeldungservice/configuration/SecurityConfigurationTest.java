package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.MeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.StatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.ValidierungsstatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @MockBean
    StatusService statusService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc api;

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
    class Status {

        @Nested
        class getStatus {

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
                val requestBody = new StatusDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
                val request = MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody));

                api.perform(request).andExpect(status().isUnauthorized());
            }

            @WithMockUser
            @Test
            void should_returnNoContent_when_callingAuthenticated() throws Exception {
                val requestBody = new StatusDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
                val request = MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody));

                api.perform(request).andExpect(status().isOk());

                Mockito.verify(statusService).setStatus(notNull(), notNull());
            }
        }
    }
}
