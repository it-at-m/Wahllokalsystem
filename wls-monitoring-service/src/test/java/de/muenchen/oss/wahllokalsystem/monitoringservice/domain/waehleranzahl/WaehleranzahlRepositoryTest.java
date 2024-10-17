package de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = { MicroServiceApplication.class },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class WaehleranzahlRepositoryTest {

    @Autowired
    private WaehleranzahlRepository waehleranzahlRepository;

    @AfterEach
    void tearDown() {
        waehleranzahlRepository.deleteAll();
    }

    /**
     * Tests if found Waehleranzahl is the one with the latest 'uhrzeit'
     */
    @Test
    void findFirstByBezirkUndWahlIDOrderByUhrzeitDesc() {
        val waehleranzahl4 = new Waehleranzahl(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 40, LocalDateTime.now().minusHours(4));
        val waehleranzahl1 = new Waehleranzahl(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 10, LocalDateTime.now());
        val waehleranzahl3 = new Waehleranzahl(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 30, LocalDateTime.now().minusHours(3));
        val waehleranzahl2 = new Waehleranzahl(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 20, LocalDateTime.now().minusHours(2));
        List<Waehleranzahl> waehleranzahlToSave = Arrays.asList(waehleranzahl2, waehleranzahl1, waehleranzahl3, waehleranzahl4);

        waehleranzahlRepository.saveAll(waehleranzahlToSave);
        val alle = waehleranzahlRepository.findAll();

        Waehleranzahl waehleranzahl = waehleranzahlRepository.findFirstByBezirkUndWahlIDOrderByUhrzeitDesc(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"));

        Assertions.assertThat(waehleranzahl).isEqualTo(waehleranzahl1);
    }
}
