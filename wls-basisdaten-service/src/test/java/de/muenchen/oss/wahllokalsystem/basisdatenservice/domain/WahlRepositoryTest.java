package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
class WahlRepositoryTest {

    @Autowired
    private WahlRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findByWahltagOrderByReihenfolge() {
        val wahlenToSave = createWahlenList();
        repository.saveAll(wahlenToSave);

        val wahltagToFind = LocalDate.now().minusMonths(1);
        val foundWahlen = repository.findByWahltagOrderByReihenfolge(wahltagToFind);
        val expectedWahlen = wahlenToSave.stream().filter(wahl -> Objects.equals(wahl.getWahltag(), wahltagToFind)).sorted(
                Comparator.comparing(Wahl::getReihenfolge)).collect(Collectors.toList());

        Assertions.assertThat(foundWahlen).isEqualTo(expectedWahlen);
    }

    @Test
    void countByWahltag(){
        val wahlenToSave = createWahlenList();
        repository.saveAll(wahlenToSave);
        val allFoundWahl = repository.findAll();
        Assertions.assertThat(allFoundWahl.size()).isEqualTo(4);

        val wahltagToFind = LocalDate.now().minusMonths(1);
        val foundNumberOfWahlByWahltag = repository.countByWahltag(wahltagToFind);
        Assertions.assertThat(foundNumberOfWahlByWahltag).isEqualTo(3);

        val wahltagToFind_0 = LocalDate.now().minusMonths(99);
        val foundNumberOfWahlByWahltag_0 = repository.countByWahltag(wahltagToFind_0);
        Assertions.assertThat(foundNumberOfWahlByWahltag_0).isEqualTo(0);
    }

    private List<Wahl> createWahlenList() {
        val wahl1 = new Wahl("wahlID1", "name1", 3L, 1L, LocalDate.now().minusMonths(1), Wahlart.BAW, new Farbe(1, 1, 1), "1");
        val wahl2 = new Wahl("wahlID2", "name2", 2L, 2L, LocalDate.now().plusMonths(1), Wahlart.EUW, new Farbe(2, 2, 2), "2");
        val wahl3 = new Wahl("wahlID3", "name3", 1L, 3L, LocalDate.now().minusMonths(1), Wahlart.VE, new Farbe(3, 3, 3), "3");
        val wahl4 = new Wahl("wahlID4", "name4", 4L, 3L, LocalDate.now().minusMonths(1), Wahlart.LTW, new Farbe(3, 3, 3), "0");

        return Arrays.asList(wahl1, wahl2, wahl3, wahl4);
    }
}
