package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class ResetWahlenControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WahlRepository wahlRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
        wahlRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class ResetWahlen {

        @Test
        void existingWahlenAreReplaced() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL);
            val oldRepositoryWahlen = createWahlEntities();
            wahlRepository.saveAll(oldRepositoryWahlen);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
            val request = MockMvcRequestBuilders.post("/businessActions/resetWahlen");
            mockMvc.perform(request).andExpect(status().isOk());

            val expectedResetedWahlen = createWahlEntities().stream().map((ResetWahlenControllerIntegrationTest.this::resetWahl)).toList();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL);
            val savedWahlen = wahlRepository.findAll();

            Assertions.assertThat(savedWahlen).isEqualTo(expectedResetedWahlen);
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
