package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.PersistingUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class WahlvorschlaegeRepositoryTest {

    @Autowired
    private WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @Autowired
    private WahlvorschlagRepository wahlvorschlagRepository;

    @Autowired
    private KandidatRepository kandidatRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void tearDown() {
        kandidatRepository.deleteAll();
        wahlvorschlagRepository.deleteAll();
        wahlvorschlaegeRepository.deleteAll();
    }

    @Nested
    class DeleteAllByBezirkUndWahlID_WahlID {

        @Test
        void should_removeWahlvorschlaegeAndChildren_when_wahlIdMatches() {
            val wahlIDToDelete = "wahlID";

            val wahlvorschlaegeToKeep = transactionTemplate.execute(status -> {
                PersistingUtils.persistWahlvorschlaege(wahlvorschlaegeRepository, wahlvorschlagRepository, kandidatRepository,
                        createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk1", wahlIDToDelete));
                PersistingUtils.persistWahlvorschlaege(wahlvorschlaegeRepository, wahlvorschlagRepository, kandidatRepository,
                        createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk2", wahlIDToDelete));

                return PersistingUtils.persistWahlvorschlaege(wahlvorschlaegeRepository, wahlvorschlagRepository, kandidatRepository,
                        createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk1", "wahlIDToKeep"));
            });

            wahlvorschlaegeRepository.deleteAllByBezirkUndWahlID_WahlID(wahlIDToDelete);

            Assertions.assertThat(wahlvorschlaegeRepository.count()).isEqualTo(1);
            Assertions.assertThat(wahlvorschlaegeRepository.existsById(wahlvorschlaegeToKeep.getId())).isTrue();

            Assertions.assertThat(wahlvorschlagRepository.count()).isEqualTo(wahlvorschlaegeToKeep.getWahlvorschlaege().size());
            wahlvorschlaegeToKeep.getWahlvorschlaege()
                    .forEach(wahlvorschlag -> Assertions.assertThat(wahlvorschlagRepository.existsById(wahlvorschlag.getId())).isTrue());

            val expectedCountKandidaten = wahlvorschlaegeToKeep.getWahlvorschlaege().stream()
                    .map(wahlvorschlag -> wahlvorschlag.getKandidaten().size())
                    .mapToLong(Integer::longValue)
                    .sum();
            Assertions.assertThat(kandidatRepository.count()).isEqualTo(expectedCountKandidaten);
            wahlvorschlaegeToKeep.getWahlvorschlaege().stream().flatMap(wahlvorschlag -> wahlvorschlag.getKandidaten().stream())
                    .forEach(kandidat -> Assertions.assertThat(kandidatRepository.existsById(kandidat.getId())).isTrue());
        }

        private Wahlvorschlaege createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID(final String wahlbezirkID, final String wahlID) {
            val wahlvorschlaege = new Wahlvorschlaege();

            wahlvorschlaege.setBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID));
            wahlvorschlaege.setStimmzettelgebietID(wahlID);

            wahlvorschlaege.setWahlvorschlaege(new HashSet<>());
            val wahlvorschlag1 = new Wahlvorschlag(null, wahlbezirkID + "_" + wahlID + "_1", null, 1L, wahlID + "_1", true, new HashSet<>());
            wahlvorschlaege.addWahlvorschlag(wahlvorschlag1);
            val wahlvorschlag2 = new Wahlvorschlag(null, wahlbezirkID + "_" + wahlID + "_2", null, 1L, wahlID + "_2", true, new HashSet<>());
            wahlvorschlaege.addWahlvorschlag(wahlvorschlag2);

            val kandidat1Vorschlag1 = new Kandidat(null, wahlvorschlag1.getIdentifikator() + "_1", null, "kandidat", 1L, true, 1L, true);
            wahlvorschlag1.addKandidat(kandidat1Vorschlag1);
            val kandidat2Vorschlag1 = new Kandidat(null, wahlvorschlag1.getIdentifikator() + "_2", null, "kandidat", 1L, true, 1L, true);
            wahlvorschlag1.addKandidat(kandidat2Vorschlag1);
            val kandidat1Vorschlag2 = new Kandidat(null, wahlvorschlag2.getIdentifikator() + "_1", null, "kandidat", 1L, true, 1L, true);
            wahlvorschlag2.addKandidat(kandidat1Vorschlag2);
            val kandidat2Vorschlag2 = new Kandidat(null, wahlvorschlag2.getIdentifikator() + "_2", null, "kandidat", 1L, true, 1L, true);
            wahlvorschlag2.addKandidat(kandidat2Vorschlag2);

            return wahlvorschlaege;
        }
    }
}
