package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.KonfigurierterWahltagClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class KopfdatenControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KopfdatenDTOMapper dtoMapper;

    @Autowired
    KopfdatenModelMapper modelMapper;

    @Autowired
    KopfdatenRepository kopfdatenRepository;

    @Autowired
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Mock
    WahltagRepository wahltagRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_KOPFDATEN);
        kopfdatenRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetKopfdaten {

        @Test
        void loadedFromExternal() throws Exception {

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_READ_KOPFDATEN);
            // mock infomanagement konfigurierterWahltag
            val infomanagementKonfigurierterWahltag = createClientKonfigurierterWahltagDTO();
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            // mock repository wahltage
            val mockedListOfEntities = createWahltagList("1");
            Mockito.when(wahltagRepository.findAllByOrderByWahltagAsc()).thenReturn(mockedListOfEntities);

            // mock EAI basisdaten
            BasisdatenDTO eaiBasisdaten = createClientBasisdatenDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + "nummerWahltag3")
                .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            val wahlID = "1_identifikatorWahltag3";
            val wahlbezirkID = "wahlbezirkID1";
            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/" + wahlID + "/" + wahlbezirkID);

            val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(responseFromController.getResponse().getContentAsString(),
                de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTO[].class);

            Assertions.assertThat(responseBodyAsDTO).isNotEmpty();
        }
    }

    private List<Wahltag> createWahltagList(String wahltagIDPrefix) {
        val wahltag1 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }

    private KonfigurierterWahltagDTO createClientKonfigurierterWahltagDTO() {

        val konfigurierterWahltag = new KonfigurierterWahltagDTO();
        konfigurierterWahltag.setWahltag(LocalDate.now().plusMonths(1));
        konfigurierterWahltag.setWahltagID("1_identifikatorWahltag3");
        konfigurierterWahltag.setWahltagStatus(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
        konfigurierterWahltag.setNummer("nummerWahltag3");

        return konfigurierterWahltag;
    }

    private BasisdatenDTO createClientBasisdatenDTO() {
        val baisdatenDTO = new BasisdatenDTO();

        val basisstrukturdatenDTO1 = new BasisstrukturdatenDTO();
        basisstrukturdatenDTO1.setWahlID("1_identifikatorWahltag3");
        basisstrukturdatenDTO1.setWahlbezirkID("wahlbezirkID1");
        basisstrukturdatenDTO1.setStimmzettelgebietID("szgID1");
        basisstrukturdatenDTO1.setWahltag(LocalDate.now().plusMonths(1));

        val wahlDTO1 = new WahlDTO();
        wahlDTO1.setIdentifikator("1_identifikatorWahl");
        wahlDTO1.setWahltag(LocalDate.now().plusMonths(1));
        wahlDTO1.setNummer("wahlNummer1");
        wahlDTO1.setName("Bundestagswahl");
        wahlDTO1.setWahlart(WahlDTO.WahlartEnum.BTW);

        val wahlbezirkDTO1 = new WahlbezirkDTO();
        wahlbezirkDTO1.setIdentifikator("1_identifikatorWahlbezirk1");
        wahlbezirkDTO1.setWahlID("wahlbezirkID1");
        wahlbezirkDTO1.setNummer("wahlNummer1");
        wahlbezirkDTO1.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
        wahlbezirkDTO1.setWahltag((LocalDate.now().plusMonths(1)));
        wahlbezirkDTO1.setWahlnummer("wahlNummer1");

        val stimmzettelgebietDTO1 = new StimmzettelgebietDTO();
        stimmzettelgebietDTO1.setIdentifikator("1_identifikatorSzg");
        stimmzettelgebietDTO1.setName("szg1");
        stimmzettelgebietDTO1.setNummer("szgNummer1");
        stimmzettelgebietDTO1.setWahltag((LocalDate.now().plusMonths(1)));
        stimmzettelgebietDTO1.setStimmzettelgebietsart(StimmzettelgebietDTO.StimmzettelgebietsartEnum.SK);

        baisdatenDTO.setBasisstrukturdaten(Set.of(basisstrukturdatenDTO1));
        baisdatenDTO.setWahlen(Set.of(wahlDTO1));
        baisdatenDTO.setWahlbezirke(Set.of(wahlbezirkDTO1));
        baisdatenDTO.stimmzettelgebiete(Set.of(stimmzettelgebietDTO1));

        return baisdatenDTO;
    }
}
