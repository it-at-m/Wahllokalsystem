package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
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
class WahlbezirkRepositoryTest {

    @Autowired
    private WahlbezirkRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findByWahltag() {
        val wahltag = LocalDate.now();
        List<Wahlbezirk> wahlbezirkeToSave = MockDataFactory.createListOfWahlbezirkEntity("", wahltag);
        repository.saveAll(wahlbezirkeToSave);
        List<Wahlbezirk> foundWahlbezirke = repository.findByWahltag(wahltag);

        Assertions.assertThat(foundWahlbezirke.size()).isEqualTo(wahlbezirkeToSave.size());
        Assertions.assertThat(foundWahlbezirke).allMatch((wbz) -> wbz.getWahltag().equals(wahltag));
    }

    @Nested
    class ExistsByWahltag {

        @Test
        void trueWhenOneExists() {
            val wahltag1 = LocalDate.now();
            val wahltag2 = LocalDate.now().plusDays(1);
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag1));
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag2));

            Assertions.assertThat(repository.existsByWahltag(wahltag1)).isTrue();
        }

        @Test
        void trueWhenMoreThanOneExists() {
            val wahltag1 = LocalDate.now();
            val wahltag2 = LocalDate.now().plusDays(1);
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag1));
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag1));
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag2));

            Assertions.assertThat(repository.existsByWahltag(wahltag1)).isTrue();
        }

        @Test
        void falseWhenZeroExists() {
            val wahltag1 = LocalDate.now();
            val wahltag2 = LocalDate.now().plusDays(1);
            repository.save(MockDataFactory.createWahlbezirkEntity(wahltag2));

            Assertions.assertThat(repository.existsByWahltag(wahltag1)).isFalse();
        }

    }

    @Test
    void deleteByWahltag() {
        val wahltag1 = LocalDate.now();
        val wahltag2 = LocalDate.now().plusDays(1);
        List<Wahlbezirk> wahlbezirkeToSave1 = MockDataFactory.createListOfWahlbezirkEntity("praefix1", wahltag1);
        repository.saveAll(wahlbezirkeToSave1);
        List<Wahlbezirk> wahlbezirkeToSave2 = MockDataFactory.createListOfWahlbezirkEntity("praefix2", wahltag2);
        repository.saveAll(wahlbezirkeToSave2);

        repository.deleteByWahltag(wahltag2);

        List<Wahlbezirk> foundWahlbezirkeAfterDelete = repository.findAll();
        Assertions.assertThat(foundWahlbezirkeAfterDelete.size()).isEqualTo(wahlbezirkeToSave1.size());
    }
}
