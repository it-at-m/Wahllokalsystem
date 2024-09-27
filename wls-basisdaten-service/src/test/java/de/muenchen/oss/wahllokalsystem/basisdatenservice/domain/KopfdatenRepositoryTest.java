package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
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
class KopfdatenRepositoryTest {

    @Autowired
    private KopfdatenRepository kopfdatenRepository;

    @AfterEach
    void tearDown() {
        kopfdatenRepository.deleteAll();
    }

    @Test
    public void kopfdatenRepositorySave() {
        val original = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1",
                Stimmzettelgebietsart.SG, "101", "München-Hadern", "Bundestagswahl", "1901");

        kopfdatenRepository.save(original);
        Optional<Kopfdaten> persisted = kopfdatenRepository.findById(new BezirkUndWahlID("wahlID1", "wahlbezirkID1"));
        Assertions.assertThat(original).isEqualTo(persisted.get());
    }

    @Test
    public void kopfdatenRepositoryDeleteAllByBezirkUndWahlID_WahlID() {
        val kopfdaten_1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1",
                Stimmzettelgebietsart.SG, "101", "München-Hadern", "Landtagswahl", "1901");
        val kopfdaten_2 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID2",
                Stimmzettelgebietsart.SG, "101", "München-Hadern", "Landtagswahl", "1901");
        val kopfdaten_3 = MockDataFactory.createKopfdatenEntityFor("wahlID2", "wahlbezirkID3",
                Stimmzettelgebietsart.SG, "101", "München-Hadern", "Landtagswahl", "1901");

        kopfdatenRepository.save(kopfdaten_1);
        kopfdatenRepository.save(kopfdaten_2);
        kopfdatenRepository.save(kopfdaten_3);

        kopfdatenRepository.deleteAllByBezirkUndWahlID_WahlID("wahlID1");

        List<Kopfdaten> persisted = kopfdatenRepository.findAll();
        Assertions.assertThat(persisted).hasSize(1);
        Assertions.assertThat(persisted.get(0)).isEqualTo(kopfdaten_3);
    }
}
