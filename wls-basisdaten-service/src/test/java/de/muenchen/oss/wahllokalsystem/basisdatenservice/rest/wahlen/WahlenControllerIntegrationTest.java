package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahlenClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class WahlenControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlDTOMapper dtoMapper;

    @Autowired
    WahlModelMapper wahlModelMapper;

    @Autowired
    WahlenClientMapper wahlenClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_WAHLEN, Authorities.ALL_AUTHORITIES_DELETE_WAHLTAGE));
        wahlRepository.deleteAll();
        wahltagRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahlen {

        @Test
        void loadedFromExternal() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            val requestDate = LocalDate.now().toString();

            val eaiWahlen = createClientSetOfWahlDTO(searchingForWahltag);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + requestDate + "&withNummer=1")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));
            wahltagRepository.save(searchingForWahltag);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + searchingForWahltag.getWahltagID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO[].class);

            val expectedResponseBody = dtoMapper
                    .fromListOfWahlModelToListOfWahlDTO(wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(eaiWahlen));

            Assertions.assertThat(responseBodyAsDTO).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
        }

        @Test
        @Transactional
        void externalDataIsPersisted() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung2", "1");
            val requestDate = LocalDate.now().toString();

            val eaiWahlen = createClientSetOfWahlDTO(searchingForWahltag);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + requestDate + "&withNummer=1")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));
            wahltagRepository.save(searchingForWahltag);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + searchingForWahltag.getWahltagID());

            api.perform(request).andExpect(status().isOk());

            val dataFromRepo = wahlRepository.findByWahltagOrderByReihenfolge(searchingForWahltag.getWahltag());

            val expectedEntities = wahlModelMapper
                    .fromListOfWahlModeltoListOfWahlEntities(wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(eaiWahlen));

            Assertions.assertThat(dataFromRepo).isEqualTo(expectedEntities);
        }

        @Test
        void loadFromRepository() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung3", "1");
            wahltagRepository.save(searchingForWahltag);

            val entitiesToFind = wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(
                    wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(createClientSetOfWahlDTO(searchingForWahltag)));
            val savedEntities = (List<Wahl>) wahlRepository.saveAll(entitiesToFind);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + searchingForWahltag.getWahltagID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTOs = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO[].class);

            val expectedResponseBody = dtoMapper.fromListOfWahlModelToListOfWahlDTO(wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(savedEntities));

            Assertions.assertThat(responseBodyAsDTOs).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
            WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));
        }

        @Test
        void technischeWlsExceptionWhenNoExternalDataFound() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung4", "1");
            val requestDate = LocalDate.now().toString();
            wahltagRepository.save(searchingForWahltag);

            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + requestDate + "&withNummer=1")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + searchingForWahltag.getWahltagID());

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(), serviceID,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostWahlen {

        @Test
        void newDataIsSet() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung5", "1");
            wahltagRepository.save(searchingForWahltag);
            val newData = createControllerListOfWahlDTO(searchingForWahltag);

            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL, Authorities.SERVICE_POST_WAHLEN);
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + searchingForWahltag.getWahltagID()).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(newData));
            api.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL);
            val savedWahlen = wahlRepository.findAll();

            Assertions.assertThat(savedWahlen)
                    .isEqualTo(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(dtoMapper.fromListOfWahlDTOtoListOfWahlModel(newData)));
        }

        @Test
        void existingWahlenAreReplaced() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung6", "1");
            wahltagRepository.save(searchingForWahltag);
            val newData = createControllerListOfWahlDTO(searchingForWahltag);
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL, Authorities.SERVICE_POST_WAHLEN, Authorities.REPOSITORY_READ_WAHL);
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + searchingForWahltag.getWahltagID()).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(newData));
            api.perform(request).andExpect(status().isOk());

            val expectedPostedWahlen = wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(dtoMapper.fromListOfWahlDTOtoListOfWahlModel(newData));
            val oldSavedWahlen = wahlRepository.findAll();

            Assertions.assertThat(oldSavedWahlen).isEqualTo(expectedPostedWahlen);
        }

        @Test
        void fachlicheWlsExceptionWhenRequestIsInvalid() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung7", "1");
            wahltagRepository.save(searchingForWahltag);
            val newData = createControllerListOfWahlDTO(searchingForWahltag);

            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL, Authorities.SERVICE_POST_WAHLEN, Authorities.REPOSITORY_READ_WAHL);
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + "     ").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(newData));
            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG.code(),
                    serviceID, ExceptionConstants.POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void fachlicheWlsExceptionWhenNotSaveableCauseOfMissingRequestbody() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL, Authorities.SERVICE_POST_WAHLEN, Authorities.REPOSITORY_READ_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung8", "1");
            wahltagRepository.save(searchingForWahltag);
            val request = MockMvcRequestBuilders.post("/businessActions/wahlen/" + searchingForWahltag.getWahltagID()).with(csrf());
            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE,
                    serviceID, "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class ResetWahlen {

        @Test
        void existingWahlenAreReplaced() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL);
            val oldRepositoryWahlen = createWahlEntities();
            wahlRepository.saveAll(oldRepositoryWahlen);

            SecurityUtils.runWith(Authorities.SERVICE_RESET_WAHLEN, Authorities.REPOSITORY_READ_WAHL);
            val request = MockMvcRequestBuilders.post("/businessActions/resetWahlen");
            api.perform(request).andExpect(status().isOk());

            val expectedResetedWahlen = createWahlEntities().stream().map((WahlenControllerIntegrationTest.this::resetWahl)).toList();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL);
            val savedWahlen = wahlRepository.findAll();

            Assertions.assertThat(savedWahlen).isEqualTo(expectedResetedWahlen);
        }
    }

    private Set<WahlDTO> createClientSetOfWahlDTO(Wahltag searchingForWahltag) {
        val wahl1 = new WahlDTO();
        wahl1.setIdentifikator("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
        wahl1.setWahltag(searchingForWahltag.getWahltag());

        val wahl2 = new WahlDTO();
        wahl2.setIdentifikator("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setWahlart(WahlDTO.WahlartEnum.LTW);
        wahl2.setWahltag(searchingForWahltag.getWahltag());

        val wahl3 = new WahlDTO();
        wahl3.setIdentifikator("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setWahlart(WahlDTO.WahlartEnum.EUW);
        wahl3.setWahltag(LocalDate.now().plusMonths(2));

        return Set.of(wahl1, wahl2, wahl3).stream().filter(wahl -> (wahl.getWahltag().equals(searchingForWahltag.getWahltag()))).collect(Collectors.toSet());
    }

    private List<de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO> createControllerListOfWahlDTO(Wahltag searchingForWahltag) {
        val wahl1 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO("wahlID1", "name1", 3L, 1L, searchingForWahltag.getWahltag(),
                Wahlart.BAW, new Farbe(1, 1, 1), "1");
        val wahl2 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO("wahlID2", "name2", 3L, 1L, searchingForWahltag.getWahltag(),
                Wahlart.BAW, new Farbe(1, 1, 1), "2");
        val wahl3 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO("wahlID3", "name3", 3L, 1L, LocalDate.now().plusMonths(2),
                Wahlart.BAW, new Farbe(1, 1, 1), "3");

        return Stream.of(wahl1, wahl2, wahl3).filter(wahl -> (wahl.wahltag().equals(searchingForWahltag.getWahltag()))).collect(Collectors.toList());
    }

    private List<Wahl> createWahlEntities() {
        val wahl1 = new Wahl();
        wahl1.setWahlID("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setFarbe(new Farbe(1, 1, 1));
        wahl1.setWahlart(Wahlart.BAW);
        wahl1.setReihenfolge(1);
        wahl1.setWaehlerverzeichnisnummer(1);
        wahl1.setWahltag(LocalDate.now().plusMonths(1));

        val wahl2 = new Wahl();
        wahl2.setWahlID("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setFarbe(new Farbe(2, 2, 2));
        wahl2.setWahlart(Wahlart.LTW);
        wahl2.setReihenfolge(2);
        wahl2.setWaehlerverzeichnisnummer(2);
        wahl2.setWahltag(LocalDate.now().plusMonths(2));

        val wahl3 = new Wahl();
        wahl3.setWahlID("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setFarbe(new Farbe(3, 3, 3));
        wahl3.setWahlart(Wahlart.EUW);
        wahl3.setReihenfolge(3);
        wahl3.setWaehlerverzeichnisnummer(3);
        wahl3.setWahltag(LocalDate.now().plusMonths(3));

        return List.of(wahl1, wahl2, wahl3);
    }

    private Wahl resetWahl(Wahl wahl) {
        wahl.setFarbe(new Farbe(0, 0, 0));
        wahl.setReihenfolge(0);
        wahl.setWaehlerverzeichnisnummer(1);
        return wahl;
    }
}
