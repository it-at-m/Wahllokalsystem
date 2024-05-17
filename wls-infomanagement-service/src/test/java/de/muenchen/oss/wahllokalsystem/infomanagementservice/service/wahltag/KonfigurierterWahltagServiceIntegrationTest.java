package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_NO_SECURITY_PROFILE })
public class KonfigurierterWahltagServiceIntegrationTest {

    @Autowired
    KonfigurierterWahltagService unitUnderTest;

    @Autowired
    KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Nested
    class SetKonfigurierterWahltag {

        @Test
        void onlyExistingKonfigurierterWahltagAreInaktiv() {
            val existingWahltag1 = new KonfigurierterWahltag(LocalDate.parse("2024-06-12"), "wahltag1", true, "nummer1");
            val existingWahltag2 = new KonfigurierterWahltag(LocalDate.parse("2024-05-12"), "wahltag2", false, "nummer2");
            val existingWahltag3 = new KonfigurierterWahltag(LocalDate.parse("2024-07-13"), "wahltag3", false, "nummer3");
            val existingWahltag4 = new KonfigurierterWahltag(LocalDate.parse("2024-08-14"), "wahltag4", false, "nummer4");
            transactionTemplate.execute((status) -> {
                konfigurierterWahltagRepository.saveAll(List.of(existingWahltag1, existingWahltag2, existingWahltag3, existingWahltag4));
                return null;
            });

            val wahltagModelToSave = new KonfigurierterWahltagModel(LocalDate.parse("2024-05-17"), "neuerWahltag", true, "neueNummer");

            unitUnderTest.setKonfigurierterWahltag(wahltagModelToSave);

            val allSavedWahltage = konfigurierterWahltagRepository.findAll();
            val lastAddedAktivWahltag = allSavedWahltage.stream().filter(wahltag -> wahltag.getWahltagID().equals(wahltagModelToSave.wahltagID())).findFirst()
                    .get();
            val otherWahltage = allSavedWahltage.stream().filter(wahltag -> !wahltag.getWahltagID().equals(wahltagModelToSave.wahltagID())).toList();

            Assertions.assertThat(otherWahltage).allSatisfy(wahltag -> Assertions.assertThat(wahltag.isActive()).isFalse());
            Assertions.assertThat(lastAddedAktivWahltag.isActive()).isTrue();
        }
    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void dataIsSortedByWahltagASC() {
            val wahltag1 = new KonfigurierterWahltag(LocalDate.parse("2024-06-12"), "wahltag1", true, "nummer1");
            val wahltag2 = new KonfigurierterWahltag(LocalDate.parse("2024-05-12"), "wahltag2", false, "nummer2");
            val wahltag3 = new KonfigurierterWahltag(LocalDate.parse("2024-07-13"), "wahltag3", false, "nummer3");
            val wahltag4 = new KonfigurierterWahltag(LocalDate.parse("2024-08-14"), "wahltag4", false, "nummer4");
            val wahltageToFindSorted = List.of(wahltag1, wahltag2, wahltag3, wahltag4);
            transactionTemplate.execute((status) -> {
                konfigurierterWahltagRepository.saveAll(wahltageToFindSorted);
                return null;
            });

            val result = unitUnderTest.getKonfigurierteWahltage();

            Assertions.assertThat(result.size()).isEqualTo(wahltageToFindSorted.size());
            Assertions.assertThat(result.get(0).wahltagID()).isEqualTo(wahltageToFindSorted.get(1).getWahltagID());
            Assertions.assertThat(result.get(1).wahltagID()).isEqualTo(wahltageToFindSorted.get(0).getWahltagID());
            Assertions.assertThat(result.get(2).wahltagID()).isEqualTo(wahltageToFindSorted.get(2).getWahltagID());
            Assertions.assertThat(result.get(3).wahltagID()).isEqualTo(wahltageToFindSorted.get(3).getWahltagID());
        }
    }
}
