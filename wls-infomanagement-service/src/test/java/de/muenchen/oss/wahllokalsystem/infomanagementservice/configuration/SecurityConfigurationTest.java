package de.muenchen.oss.wahllokalsystem.infomanagementservice.configuration;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationSetDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import java.util.Optional;
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
    KonfigurierterWahltagService konfigurierterWahltagService;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    KonfigurationService konfigurationService;

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

    @Nested
    class Konfiguration {

        @Test
        @WithAnonymousUser
        void accessGetKonfigurationWithKeyUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/ABSCHLUSSTEXT");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetKonfigurationWithKeyAuthorizedThenNoContent() throws Exception {
            Mockito.when(konfigurationService.getKonfiguration(any())).thenReturn(Optional.empty());

            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/ABSCHLUSSTEXT");

            api.perform(request).andExpect(status().isNoContent());
        }

        @Test
        @WithAnonymousUser
        void accessPostKonfigurationWithKeyUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/konfiguration/ABSCHLUSSTEXT").with(csrf());

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostKonfigurationWithKeyAuthorizedThenOk() throws Exception {
            val requestBody = new KonfigurationSetDTO("wert", "beschreibung", "default");
            val request = MockMvcRequestBuilders.post("/businessActions/konfiguration/ABSCHLUSSTEXT").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessGetKonfigurationenUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetKonfigurationenAuthorizedThenOk() throws Exception {
            Mockito.when(konfigurationService.getKonfiguration(any())).thenReturn(Optional.empty());

            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration");

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessGetKennbuchstabenListenUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/kennbuchstaben");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetKennbuchstabenListenAuthorizedThenOk() throws Exception {
            Mockito.when(konfigurationService.getKonfiguration(any())).thenReturn(Optional.empty());

            val request = MockMvcRequestBuilders.get("/businessActions/kennbuchstaben");

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessGetKonfigurationUnauthorizedThenOk() throws Exception {
            Mockito.when(konfigurationService.getKonfigurationUnauthorized(any())).thenReturn(Optional.of(KonfigurationModel.builder().build()));

            val request = MockMvcRequestBuilders.get("/businessActions/konfigurationUnauthorized/WILLKOMMENSTEXT");

            api.perform(request).andExpect(status().isOk());
        }

    }

}
