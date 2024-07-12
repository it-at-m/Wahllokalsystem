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
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ "db-h2" })
class WahlbezirkRepositoryTest {

    @Autowired
    WahltageRepository wahltageRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    StimmzettelgebietRepository stimmzettelgebietRepository;

    @Autowired
    WahlbezirkRepository unitUnderTest;

    @AfterEach
    void tearDown() {
        unitUnderTest.deleteAll();
        stimmzettelgebietRepository.deleteAll();
        wahlRepository.deleteAll();
        wahltageRepository.deleteAll();
    }

    @Nested
    class FindWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByWahltagAndNummer {

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

            val szg1 = stimmzettelgebietRepository.save(new Stimmzettelgebiet("", "", Stimmzettelgebietsart.SK, wahl1));
            val szg2 = stimmzettelgebietRepository.save(new Stimmzettelgebiet("", "", Stimmzettelgebietsart.SK, wahl2));
            val szg3 = stimmzettelgebietRepository.save(new Stimmzettelgebiet("", "", Stimmzettelgebietsart.SK, wahl3));
            val szg4 = stimmzettelgebietRepository.save(new Stimmzettelgebiet("", "", Stimmzettelgebietsart.SK, wahl4));

            val wahlbezirk1ToFind = unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "wbz1", szg1, 0, 0, 0));
            val wahlbezirk2ToFind = unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "wbz2", szg1, 0, 0, 0));
            unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "wbz3", szg2, 0, 0, 0));
            unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "wbz4", szg3, 0, 0, 0));
            unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "wbz5", szg4, 0, 0, 0));

            val result = unitUnderTest.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByWahltagAndNummer(wahltag, nummer);

            val expectedResult = new Wahlbezirk[] {
                    wahlbezirk1ToFind,
                    wahlbezirk2ToFind
            };
            Assertions.assertThat(result).containsOnly(expectedResult);
        }
    }

    @Nested
    class FindWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByID {

        @Test
        void dataFound() {
            val wahltag = wahltageRepository.save(new Wahltag(LocalDate.now(), "", "nummer"));
            val wahl = wahlRepository.save(new Wahl("", Wahlart.BTW, wahltag));
            val szg = stimmzettelgebietRepository.save(new Stimmzettelgebiet("", "", Stimmzettelgebietsart.SK, wahl));

            val wahlbezirkToFind = unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "nummer1", szg, 1, 2, 3));
            unitUnderTest.save(new Wahlbezirk(WahlbezirkArt.UWB, "nummer2", szg, 1, 2, 3));

            val result = unitUnderTest.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByID(wahlbezirkToFind.getId());

            Assertions.assertThat(result).containsOnly(wahlbezirkToFind);
        }
    }

}
