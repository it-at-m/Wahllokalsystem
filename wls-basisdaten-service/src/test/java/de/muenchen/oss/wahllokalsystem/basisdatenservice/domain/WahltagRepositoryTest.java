package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class WahltagRepositoryTest {

    @Autowired
    private WahltagRepository repository;

    @Test
    void findAll() {
        List<Wahltag> wahltageToSave1 = createWahltagList("1");
        List<Wahltag> wahltageToSave2 = createWahltagList("2");
        List<Wahltag> wahltageToSave3 = createWahltagList("3");
        repository.saveAll(wahltageToSave1);
        repository.saveAll(wahltageToSave2);
        repository.saveAll(wahltageToSave3);
        List<Wahltag> foundWahltage = repository.findAll();
        Assertions.assertThat(foundWahltage).containsAll(wahltageToSave1).containsAll(wahltageToSave2).containsAll(wahltageToSave3);
    }

    @Test
    void findById() {
        List<Wahltag> wahltageToSave1 = createWahltagList("X");
        repository.saveAll(wahltageToSave1);
        Wahltag wahltag3 = wahltageToSave1.get(2);
        Optional<Wahltag> foundWahltagById = repository.findById("X_identifikatorWahltag3");

        Assertions.assertThat(foundWahltagById).isPresent();
        Assertions.assertThat(foundWahltagById.get()).isEqualTo(wahltag3);
    }

    /**
     * Tests if searched returned Wahltags are right sorted
     */
    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void findAllByOrderByWahltagAsc() {
        List<Wahltag> wahltageToSave = createWahltagList("1");
        repository.saveAll(wahltageToSave);
        List<Wahltag> foundWahltage = repository.findAllByOrderByWahltagAsc();

        wahltageToSave.sort(
                Comparator
                        .comparing((Wahltag w) -> w.getWahltag())
                        .thenComparing((Wahltag w) -> w.getWahltag()));

        Assertions.assertThat(wahltageToSave).isEqualTo(foundWahltage);
    }

    private List<Wahltag> createWahltagList(String pIndex) {
        val wahltag4 = new Wahltag(pIndex + "_identifikatorWahltag4", LocalDate.now().plusMonths(5), "beschreibungWahltag4", "nummerWahltag4");
        val wahltag1 = new Wahltag(pIndex + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag3 = new Wahltag(pIndex + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");
        val wahltag2 = new Wahltag(pIndex + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");

        List<Wahltag> myModifiableList = Arrays.asList(wahltag4, wahltag1, wahltag3, wahltag2);
        return myModifiableList;
    }
}
