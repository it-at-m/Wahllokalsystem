package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class)
class WahlRepositoryTest {

    @Autowired
    WahltageRepository wahltageRepository;

    @Autowired
    WahlRepository wahlRepository;

    @AfterEach
    void tearDown() {
        wahlRepository.deleteAll();
    }

    @Nested
    class FindByWahltagTagAndWahltagNummer {

        @Test
        void dataFound() {
            val wahltag = LocalDate.now();
            val nummer = "1";

            val wahltag1 = wahltageRepository.save(new Wahltag(wahltag, "", nummer));
            val wahltag2 = wahltageRepository.save(new Wahltag(wahltag.minusDays(1), "", nummer));
            val wahltag3 = wahltageRepository.save(new Wahltag(wahltag.plusDays(1), "", nummer));
            val wahltag4 = wahltageRepository.save(new Wahltag(wahltag, "", nummer + "x"));

            val wahl1ToFind = wahlRepository.save(new Wahl("wahl1", Wahlart.BTW, wahltag1, ""));
            val wahl2ToFind = wahlRepository.save(new Wahl("wahl2", Wahlart.BTW, wahltag1, ""));
            wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag2, ""));
            wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag3, ""));
            wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag4, ""));

            val result = wahlRepository.findByWahltagTagAndWahltagNummer(wahltag, nummer);

            val expectedResult = new Wahl[] {
                    wahl1ToFind,
                    wahl2ToFind
            };

            Assertions.assertThat(result).containsOnly(expectedResult);
        }
    }

}
