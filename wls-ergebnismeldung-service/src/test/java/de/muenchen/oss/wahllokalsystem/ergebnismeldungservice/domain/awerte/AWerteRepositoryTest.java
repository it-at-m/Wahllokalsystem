package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class AWerteRepositoryTest {

    @Autowired
    private AWerteRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    class FindByBezirkUndWahlID_WahlbezirkID {

        @Test
        void should_returnAWerte_when_wahlbezirkIDIsGiven() {
            val wahlbezirkIdToFind = "wahlbezirkID1";
            val aWerteToFind = new AWerte(new BezirkUndWahlID("wahlID1", wahlbezirkIdToFind), 2, 3L);
            val aWerteToSave = List.of(
                    aWerteToFind,
                    new AWerte(new BezirkUndWahlID("wahlID1", "wahlbezirkID2"), 5, 6L),
                    new AWerte(new BezirkUndWahlID("wahlID1", "wahlbezirkID3"), 10, 11L),
                    new AWerte(new BezirkUndWahlID("wahlID1", "wahlbezirkID4"), 15, 16L)

            );

            repository.saveAll(aWerteToSave);

            val result = repository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkIdToFind);
            Assertions.assertThat(result).hasSize(1);
            Assertions.assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(aWerteToFind);
        }
    }

    @Test
    void should_returnAWerteForDifferentWahlIDs_when_wahlbezirkIDIsGiven() {
        val wahlbezirkIDToFind = "wahlbezirkID";
        val aWerteToSave = List.of(
                new AWerte(new BezirkUndWahlID("wahlID1", wahlbezirkIDToFind), 2, 3L),
                new AWerte(new BezirkUndWahlID("wahlID1", "wahlbezirkID2"), 4, 5L),
                new AWerte(new BezirkUndWahlID("wahlID1", "wahlbezirkID3"), 5, 6L),
                new AWerte(new BezirkUndWahlID("wahlID2", wahlbezirkIDToFind), 6, 7L),
                new AWerte(new BezirkUndWahlID("wahlID3", wahlbezirkIDToFind), 7, 8L));

        repository.saveAll(aWerteToSave);

        val result = repository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkIDToFind);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result).allSatisfy(aWert -> {
            Assertions.assertThat(aWert.getBezirkUndWahlID().getWahlbezirkID()).isEqualTo(wahlbezirkIDToFind);
        });
    }
}
