package de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.AWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.BWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnis;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.ErgebnisRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Meldungsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.UngueltigeStimmzettel;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.WahlbriefeWerte;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.WahlbriefeWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung.ErgebnismeldungMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung.ErgebnismeldungValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Set;
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
public class ErgebnismeldungControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceInfoOid;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ErgebnisRepository ergebnismeldungRepository;

    @Autowired
    ErgebnismeldungMapper ergebnismeldungMapper;

    @SpyBean
    ErgebnismeldungValidator ergebnismeldungValidator;

    @Autowired
    ExceptionFactory exceptionFactory;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        ergebnismeldungRepository.deleteAll();
    }

    @Nested
    class SaveErgebnismeldung {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_ERGEBNISMELDUNG)
        @Transactional
        void meldungIsSaved() throws Exception {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val meldungsart = MeldungsartDTO.NIEDERSCHRIFT;
            val aWerte = new AWerteDTO(3L, 2L);
            val bWerte = new BWerteDTO(4L, 3L, 2L);
            val wahlbriefeWerte = new WahlbriefeWerteDTO(3L);
            val ungueltigeStimmzettelDTOList = Set.of(new UngueltigeStimmzettelDTO("test1", 4L, "wahlvorschlagID1"),
                    new UngueltigeStimmzettelDTO("test2", 5L, "wahlvorschlagID2"));
            val ungueltigeStimmzettelAnzahl = 4L;
            val ergebnisse = Set.of(new ErgebnisDTO("test1", 5L, 3L, "wahlvorschlagID1", "kandidatID1"),
            new ErgebnisDTO("test2", 6L, 4L, "wahlvorschlagID2", "kandidatID2"));
            val wahlart = WahlartDTO.BTW;

            val requestBody = new ErgebnismeldungDTO(wahlID, wahlbezirkID, meldungsart, aWerte, bWerte, wahlbriefeWerte, ungueltigeStimmzettelDTOList, ungueltigeStimmzettelAnzahl, ergebnisse, wahlart);
            val request = MockMvcRequestBuilders.post("/ergebnismeldung").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val aWerte2 = new AWerte();
            aWerte2.setA1(3L);
            aWerte2.setA2(2L);

            val bWerte2 = new BWerte();
            bWerte2.setB(4L);
            bWerte2.setB1(3L);
            bWerte2.setB2(2L);

            val wahlbriefeWerte2 = new WahlbriefeWerte();
            wahlbriefeWerte2.setZurueckgewiesenGesamt(3L);

            val ungueltigeStimmzettel2 = new UngueltigeStimmzettel();
            ungueltigeStimmzettel2.setAnzahl(5L);
            ungueltigeStimmzettel2.setStimmenart("test1");
            ungueltigeStimmzettel2.setWahlvorschlagID("wahlvorschlagID1");

            val ungueltigeStimmzettel3 = new UngueltigeStimmzettel();
            ungueltigeStimmzettel2.setAnzahl(5L);
            ungueltigeStimmzettel2.setStimmenart("test1");
            ungueltigeStimmzettel2.setWahlvorschlagID("wahlvorschlagID1");

            val ungueltigeStimmzettelList = Set.of(ungueltigeStimmzettel2,
                    ungueltigeStimmzettel3);
            val ergebnis1 = new Ergebnis();
            val ergebnis2 = new Ergebnis();

            ergebnis1.setStimmenart("test1");
            ergebnis1.setWahlvorschlagsordnungszahl(5L);
            ergebnis1.setErgebnis(3L);
            ergebnis1.setKandidatID("kandidatID1");
            ergebnis1.setWahlvorschlagID("wahlvorschlagID1");

            ergebnis2.setStimmenart("test2");
            ergebnis2.setWahlvorschlagsordnungszahl(5L);
            ergebnis2.setErgebnis(3L);
            ergebnis2.setKandidatID("kandidatID2");
            ergebnis2.setWahlvorschlagID("wahlvorschlagID2");

            val ergebnisse2 = Set.of(ergebnis1,ergebnis2);

            val savedErgebnismeldung = ergebnismeldungRepository.findAll().iterator().next();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();

            val expectedSavedErgebnismeldung = new Ergebnismeldung(requestBody.wahlID(), requestBody.wahlbezirkID(), Meldungsart.NIEDERSCHRIFT, aWerte2,
                    bWerte2, wahlbriefeWerte2, ungueltigeStimmzettelList, requestBody.ungueltigeStimmzettelAnzahl(),ergebnisse2, Wahlart.BTW);

            Assertions.assertThat(savedErgebnismeldung).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedSavedErgebnismeldung);
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
            Mockito.doThrow(mockedValidationException).when(ergebnismeldungValidator).validDTOToSetOrThrow(any());

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, "999", "EAI-SERVICE", "");

            Assertions.assertThat(responseBodyDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(responseBodyDTO.message()).contains(mockedExceptionMessage);
        }

    }
}
