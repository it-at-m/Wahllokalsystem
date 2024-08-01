package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahlenClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahltageClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage.WahltageDTOMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    WahlenClientMapper wahlenClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_WAHLEN);
        wahlRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahlen {

        @Test
        void loadedFromExternal() throws Exception {
            var searchingForWahltag = new Wahltag("wahltagID",LocalDate.now(),"beschreibung1", "1" );
            val requestDate = LocalDate.now().toString();

            val eaiWahlen = createClientSetOfWahlDTO(searchingForWahltag);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + requestDate + "&withNummer=1")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));
            wahltagRepository.save(searchingForWahltag);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + searchingForWahltag.getWahltagID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO[].class);

            val fromWahlenclientmapper = wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(eaiWahlen);

            val expectedResponseBody = dtoMapper.fromListOfWahlModelToListOfWahlDTO(fromWahlenclientmapper);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        void existingWahlenAreReplaced() throws Exception {
//            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL);
//            val oldRepositoryWahlen = createWahlEntities();
//            wahlRepository.saveAll(oldRepositoryWahlen);
//
//            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
//            val request = MockMvcRequestBuilders.post("/businessActions/resetWahlen");
//            mockMvc.perform(request).andExpect(status().isOk());
//
//            val expectedResetedWahlen = createWahlEntities().stream().map((WahlenControllerIntegrationTest.this::resetWahl)).toList();
//
//            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL);
//            val savedWahlen = wahlRepository.findAll();
//
//            Assertions.assertThat(savedWahlen).isEqualTo(expectedResetedWahlen);
        }
    }

    @Nested
    class ResetWahlen {

        @Test
        void existingWahlenAreReplaced() throws Exception {
//            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL);
//            val oldRepositoryWahlen = createWahlEntities();
//            wahlRepository.saveAll(oldRepositoryWahlen);
//
//            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
//            val request = MockMvcRequestBuilders.post("/businessActions/resetWahlen");
//            mockMvc.perform(request).andExpect(status().isOk());
//
//            val expectedResetedWahlen = createWahlEntities().stream().map((WahlenControllerIntegrationTest.this::resetWahl)).toList();
//
//            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL);
//            val savedWahlen = wahlRepository.findAll();
//
//            Assertions.assertThat(savedWahlen).isEqualTo(expectedResetedWahlen);
        }
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
        wahl1.setWahltag(LocalDate.now());

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

    private Set<WahlDTO> createClientSetOfWahlDTO(Wahltag searchingForWahltag){
        val wahl1 = new WahlDTO();
        wahl1.setIdentifikator("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
        wahl1.setWahltag(LocalDate.now());

        val wahl2 = new WahlDTO();
        wahl2.setIdentifikator("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setWahlart(WahlDTO.WahlartEnum.LTW);
        wahl2.setWahltag(LocalDate.now());

        val wahl3 = new WahlDTO();
        wahl3.setIdentifikator("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setWahlart(WahlDTO.WahlartEnum.EUW);
        wahl3.setWahltag(LocalDate.now().plusMonths(2));

        return Set.of(wahl1, wahl2, wahl3).stream().filter(wahl -> (wahl.getWahltag().equals(searchingForWahltag.getWahltag()))).collect(Collectors.toSet());
    }
}