package de.muenchen.oss.wahllokalsystem.adminservice.configuration;

import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.FarbeDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage.WahltageService;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenService;
import java.time.LocalDate;
import java.util.List;
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

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WahltermindatenService wahltermindatenService;

    @MockBean
    WahltageService wahltageService;

    @MockBean
    WahlenService wahlenService;

    @Test
    void should_returnStatusUnauthorized_when_accessingSecuredResourceRoot() throws Exception {
        api.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_returnStatusUnauthorized_when_accessingSecuredResourceActuator() throws Exception {
        api.perform(get("/actuator"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_returnStatusOk_when_accessingUnsecuredResourceActuatorHealth() throws Exception {
        api.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void should_returnStatusOk_when_accessingUnsecuredResourceActuatorInfo() throws Exception {
        api.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void should_returnStatusOk_when_accessingUnsecuredResourceActuatorMetrics() throws Exception {
        api.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    void should_returnStatusOk_when_accessingUnsecuredResourceV3ApiDocs() throws Exception {
        api.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void should_returnStatusOk_when_accessingUnsecuredResourceSwaggerUi() throws Exception {
        api.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Nested
    class LoadWahltermindaten {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingAnonymous() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.post("/businessActions/importWahltermindaten/" + wahltagID).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON);

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnOk_when_callingAuthenticated() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.post("/businessActions/importWahltermindaten/" + wahltagID).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON);

            api.perform(request).andExpect(status().isOk());

            Mockito.verify(wahltermindatenService).loadWahltermindaten(wahltagID);
        }
    }

    @Nested
    class GetWahltage {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingAnonymous() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnNoContent_when_callingAuthenticated() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            api.perform(request).andExpect(status().isNoContent());

            Mockito.verify(wahltageService).getWahltage();
        }
    }

    @Nested
    class GetWahlen {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingAnonymous() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + wahltagID);

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnNoContent_when_callingAuthenticated() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + wahltagID);

            api.perform(request).andExpect(status().isNoContent());

            Mockito.verify(wahlenService).getWahlen(wahltagID);
        }
    }

    @Nested
    class UpdateWahlen {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingAnonymous() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + wahltagID)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON);

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnOk_when_callingAuthenticated() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + wahltagID)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(List.of(
                            new WahlDTO("wahlID1", "name1", 3L, 1L, LocalDate.now(), WahlartDTO.BAW, new FarbeDTO(1, 1, 1)))));

            api.perform(request).andExpect(status().isOk());
        }
    }

}
