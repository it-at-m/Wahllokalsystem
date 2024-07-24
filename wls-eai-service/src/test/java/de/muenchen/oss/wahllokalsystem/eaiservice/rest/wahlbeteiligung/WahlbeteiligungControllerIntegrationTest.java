package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.Wahlbeteiligung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.WahlbeteiligungRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung.WahlbeteiligungMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung.WahlbeteiligungValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class WahlbeteiligungControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceInfoOid;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlbeteiligungRepository wahlbeteiligungRepository;

    @Autowired
    WahlbeteiligungMapper wahlbeteiligungMapper;

    @SpyBean
    WahlbeteiligungValidator wahlbeteiligungValidator;

    @Autowired
    ExceptionFactory exceptionFactory;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        wahlbeteiligungRepository.deleteAll();
    }

    @Nested
    class SaveWahlbeteiligungsMeldung {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_WAHLBETEILIGUNG)
        @Transactional
        void meldungIsSaved() throws Exception {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val requestBody = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);
            val request = MockMvcRequestBuilders.post("/wahlbeteiligung").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val savedWahlbeteiligung = wahlbeteiligungRepository.findAll().iterator().next();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();

            val expectedSavedWahlbeteiligung = new Wahlbeteiligung(requestBody.wahlID(), requestBody.wahlbezirkID(), requestBody.anzahlWaehler(),
                    requestBody.meldeZeitpunkt());

            Assertions.assertThat(savedWahlbeteiligung).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedSavedWahlbeteiligung);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_WAHLBETEILIGUNG)
        @Transactional
        void validationExceptionOccurredAndIsMapped() throws Exception {

            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val requestBody = new WahlbeteiligungsMeldungDTO(null, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);
            val request = MockMvcRequestBuilders.post("/wahlbeteiligung").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            val mockedExceptionMessage = "mocked null pointer exception";
            val mockedValidationException = new NullPointerException(mockedExceptionMessage);
            Mockito.doThrow(mockedValidationException).when(wahlbeteiligungValidator).validDTOToSetOrThrow(any());

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, "999", "EAI-SERVICE", "");

            Assertions.assertThat(responseBodyDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(responseBodyDTO.message()).contains(mockedExceptionMessage);
        }

    }

}
