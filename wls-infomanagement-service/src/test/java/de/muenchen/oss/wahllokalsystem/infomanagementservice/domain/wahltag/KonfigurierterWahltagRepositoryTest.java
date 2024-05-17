package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_NO_SECURITY_PROFILE })
class KonfigurierterWahltagRepositoryTest {

    @Autowired
    KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    @Nested
    class SetExistingKonfigurierteWahltageInaktiv {

        @Test
        @Transactional
        void verifyAllKonfigurierteWahltageAreInaktiv() {
            val wahltageToSave = List.of(new KonfigurierterWahltag(LocalDate.parse("2024-01-01"), "wahltag1", true, "nummer1"),
                    new KonfigurierterWahltag(LocalDate.parse("2024-01-02"), "wahltag2", false, "nummer2"),
                    new KonfigurierterWahltag(LocalDate.parse("2024-01-03"), "wahltag3", false, "nummer3"),
                    new KonfigurierterWahltag(LocalDate.parse("2024-01-04"), "wahltag4", true, "nummer4"));
            konfigurierterWahltagRepository.saveAll(wahltageToSave);

            konfigurierterWahltagRepository.setExistingKonfigurierteWahltageInaktiv();

            val konfigurierteWahltageInRepo = konfigurierterWahltagRepository.findAll();

            Assertions.assertThat(konfigurierteWahltageInRepo)
                    .allSatisfy(wahltag -> Assertions.assertThat(wahltag.isActive()).isFalse());
        }
    }

}
