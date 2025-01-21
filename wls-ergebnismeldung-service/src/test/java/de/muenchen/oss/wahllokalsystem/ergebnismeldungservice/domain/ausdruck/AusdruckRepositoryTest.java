package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.TimePrecisionComparators.INSTANT_PRECISION_MILLISECONDS;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import java.time.Instant;
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
        classes = { MicroServiceApplication.class }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class AusdruckRepositoryTest {

    @Autowired
    AusdruckRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    class FindOneByWahlUndBezirkIDUndMeldungsart {

        @Test
        void should_returnAusdruck_when_wahlUndBezirkIDUndMeldungsartIsGiven() {
            val wahlUndBezirkIDUndMeldungsart = new WahlUndBezirkIDUndMeldungsart("wahlbezirkID01", "wahlID01", Meldungsart.V1);
            val ausdruckToFind = new Ausdruck(wahlUndBezirkIDUndMeldungsart, "Testcontent", Instant.now());
            val ausdruckeToSave = List.of(
                    ausdruckToFind,
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID02", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()),
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID03", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()),
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID04", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()));

            repository.saveAll(ausdruckeToSave);

            val result = repository.findOneByWahlUndBezirkIDUndMeldungsart(wahlUndBezirkIDUndMeldungsart);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result).usingRecursiveComparison().withComparatorForType(INSTANT_PRECISION_MILLISECONDS, Instant.class)
                    .isEqualTo(ausdruckToFind);
        }
    }

    @Nested
    class FindAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID {

        @Test
        void should_returnAusdruck_when_wahlUndBezirkIDUndMeldungsartIsGiven() {
            val wahlIdToFind = "wahlId01";
            val wahlbezirkIdToFind = "wahlbezirkID01";
            val ausdruckToFind1 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkIdToFind, wahlIdToFind, Meldungsart.V1), "Testcontent",
                    Instant.now());
            val ausdruckToFind2 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkIdToFind, wahlIdToFind, Meldungsart.V3), "Testcontent",
                    Instant.now());
            val ausdruckeToSave = List.of(
                    ausdruckToFind1,
                    ausdruckToFind2,
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID02", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()),
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID03", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()),
                    new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID04", "wahlID01", Meldungsart.V1),
                            "Testcontent", Instant.now()));

            repository.saveAll(ausdruckeToSave);

            val result = repository.findAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID(wahlIdToFind,
                    wahlbezirkIdToFind);
            Assertions.assertThat(result).hasSize(2);
            Assertions.assertThat(result).allSatisfy(ausdruck -> {
                Assertions.assertThat(ausdruck.getWahlUndBezirkIDUndMeldungsart().getWahlID()).isEqualTo(wahlIdToFind);
                Assertions.assertThat(ausdruck.getWahlUndBezirkIDUndMeldungsart().getWahlbezirkID()).isEqualTo(wahlbezirkIdToFind);
            });
        }
    }

}
