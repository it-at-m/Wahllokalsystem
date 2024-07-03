package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class)
class StimmzettelgebietRepositoryTest {

    @Autowired
    WahltageRepository wahltageRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    StimmzettelgebietRepository stimmzettelgebietRepository;

    @Nested
    class FindByWahlWahltagTagAndWahlNummer {

        @Test
        void dataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val wahltag1 = wahltageRepository.save(new Wahltag(wahltag, "", nummer));
            val wahltag2 = wahltageRepository.save(new Wahltag(wahltag.minusDays(1), "", nummer));
            val wahltag3 = wahltageRepository.save(new Wahltag(wahltag.plusDays(1), "", nummer));
            val wahltag4 = wahltageRepository.save(new Wahltag(wahltag, "", nummer + "x"));

            val wahl1 = wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag1));
            val wahl2 = wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag2));
            val wahl3 = wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag3));
            val wahl4 = wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag4));

            val sgz1ToFind = stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "", Stimmzettelgebietsart.SK, wahl1));
            val sgz2ToFind = stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz2", "", Stimmzettelgebietsart.SK, wahl1));
            stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "", Stimmzettelgebietsart.SK, wahl2));
            stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "", Stimmzettelgebietsart.SK, wahl3));
            stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "", Stimmzettelgebietsart.SK, wahl4));

            val result = stimmzettelgebietRepository.findByWahlWahltagTagAndWahlWahltagNummer(wahltag, nummer);

            val expectedResult = new Stimmzettelgebiet[] {
                    sgz1ToFind,
                    sgz2ToFind
            };

            Assertions.assertThat(result).containsOnly(expectedResult);
        }
    }

}
