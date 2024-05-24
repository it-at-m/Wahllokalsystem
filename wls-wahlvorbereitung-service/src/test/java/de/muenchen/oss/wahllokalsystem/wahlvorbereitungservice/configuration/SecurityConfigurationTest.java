package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.configuration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisService;
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
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
public class SecurityConfigurationTest {

    @MockBean
    UrnenwahlvorbereitungService urnenwahlvorbereitungService;

    @MockBean
    WaehlerverzeichnisService waehlerverzeichnisService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void accessUnsecuredResourceV3ApiDocsThenOk() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceSwaggerUiThenOk() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Nested
    class Urnenwahlvorbereitung {

        @Test
        @WithAnonymousUser
        void accessGetUrnenwahlvorbereitungUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/urnenwahlVorbereitung/wahlbezirkID");

            mockMvc.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetUrnenwahlvorbereitungAuthorizedThenNoContent() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/urnenwahlVorbereitung/wahlbezirkID");

            mockMvc.perform(request).andExpect(status().isNoContent());
        }

        @Test
        @WithAnonymousUser
        void accessPostUrnenwahlvorbereitungUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/urnenwahlVorbereitung/wahlbezirkID").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content("{}");

            mockMvc.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostUrnenwahlvorbereitungAuthorizedThenIsCreated() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/urnenwahlVorbereitung/wahlbezirkID").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content("{}");

            mockMvc.perform(request).andExpect(status().isCreated());
        }
    }

    @Nested
    class Waehlerverzeichnis {

        @Test
        @WithAnonymousUser
        void accessGetWaehlerverzeichnisUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/waehlerverzeichnis/waehlerbezirkID/1");

            mockMvc.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetWaehlerverzeichnisAuthorizedThenIsNoContent() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/waehlerverzeichnis/waehlerbezirkID/1");

            mockMvc.perform(request).andExpect(status().isNoContent());
        }

        @Test
        @WithAnonymousUser
        void accessPostWaehlerverzeichnisUnauthorizedThenUnauthorized() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/waehlerverzeichnis/waehlerbezirkID/1")
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}");

            mockMvc.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostWaehlerverzeichnisAuthorizedThenIsCreated() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/waehlerverzeichnis/waehlerbezirkID/1")
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}");

            mockMvc.perform(request).andExpect(status().isCreated());
        }
    }
}
