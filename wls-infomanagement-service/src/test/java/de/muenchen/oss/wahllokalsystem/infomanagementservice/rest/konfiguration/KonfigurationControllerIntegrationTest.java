package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.WithMockUserAsJwt;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class KonfigurationControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Nested
    class GetKonfiguration {

        @AfterEach
        void tearDown() {
            SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_KONFIGURATION);
            konfigurationRepository.deleteAll();
        }

        @Test
        @WithMockUserAsJwt(authorities = { Authorities.SERVICE_GET_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION })
        void emptyResponse() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/WILLKOMMENSTEXT");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION }
        )
        void dataFound() throws Exception {
            val konfiguration1 = new Konfiguration("schluessel1", "wert1", "beschreibung1", "standardwert1");
            val konfiguration2 = new Konfiguration("schluessel2", "wert2", "beschreibung2", "standardwert2");
            val konfigurationToFind = new Konfiguration("WILLKOMMENSTEXT", "hello world", "A long time ago in a galaxy far, far away", "a new hope");

            konfigurationRepository.save(konfiguration1);
            konfigurationRepository.save(konfiguration2);
            konfigurationRepository.save(konfigurationToFind);

            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/WILLKOMMENSTEXT");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), KonfigurationDTO.class);

            val expectedResponseBody = new KonfigurationDTO(konfigurationToFind.getSchluessel(), konfigurationToFind.getWert(),
                    konfigurationToFind.getBeschreibung(), konfigurationToFind.getStandardwert());

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }
}
