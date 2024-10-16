package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.WahltagRepository;
import java.time.LocalDate;
import java.util.Arrays;
import lombok.val;
import org.assertj.core.api.Assertions;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
class WahltagRepositoryTest {

    @Autowired
    private WahltagRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    /**
     * Tests if searched returned Wahltags are right sorted
     */
    @Test
    void findAllByOrderByWahltagAsc() {
        List<Wahltag> wahltageToSave = createWahltagList();
        repository.saveAll(wahltageToSave);
        List<Wahltag> foundWahltage = repository.findAllByOrderByWahltagAsc();

        wahltageToSave.sort(
                Comparator
                        .comparing(Wahltag::getWahltag)
                        .thenComparing(Wahltag::getWahltag));

        Assertions.assertThat(wahltageToSave).isEqualTo(foundWahltage);
    }

    private List<Wahltag> createWahltagList() {
        val wahltag4 = new Wahltag("_identifikatorWahltag4", LocalDate.now().plusMonths(5), "beschreibungWahltag4", "nummerWahltag4");
        val wahltag1 = new Wahltag("_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag3 = new Wahltag("_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");
        val wahltag2 = new Wahltag("_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");

        return Arrays.asList(wahltag4, wahltag1, wahltag3, wahltag2);
    }
}
