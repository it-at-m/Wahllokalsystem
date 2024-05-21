package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.configuration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @Autowired
    MockMvc mockMvc;

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
    }
}
