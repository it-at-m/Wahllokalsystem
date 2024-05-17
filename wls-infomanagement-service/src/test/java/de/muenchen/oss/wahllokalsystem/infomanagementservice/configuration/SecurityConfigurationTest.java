package de.muenchen.oss.wahllokalsystem.infomanagementservice.configuration;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @MockBean
    KonfigurierterWahltagService konfigurierterWahltagService;

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
    class KonfigurierterWahltag {

        @Test
        @WithAnonymousUser
        void accessGetKonfigurierterWahltagUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetKonfigurierterWahltagAuthorizedThenNoContent() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");

            api.perform(request).andExpect(status().isNoContent());
        }

        @Test
        @WithAnonymousUser
        void accessPostKonfigurierterWahltagUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content("{}");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostKonfigurierterWahltagAuthorizedThenOk() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content("{}");

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessDeleteKonfigurierterWahltagUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.delete("/businessActions/konfigurierterWahltag/wahltagID").with(csrf());

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessDeleteKonfigurierterWahltagAuthorizedThenOk() throws Exception {
            val request = MockMvcRequestBuilders.delete("/businessActions/konfigurierterWahltag/wahltagID").with(csrf());

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessGetKonfigurierteWahltageUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierteWahltage");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetKonfigurierteWahltageAuthorizedThenOk() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierteWahltage");

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessGetLoginCheckUnauthorizedThenOk() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/loginCheck/wahltagID");

            api.perform(request).andExpect(status().isOk());
        }

    }

}
