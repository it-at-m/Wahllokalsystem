package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumoption;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.HashSet;
import java.util.UUID;
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
class ReferendumvorlagenRepositoryTest {

    @Autowired
    private ReferendumvorlagenRepository referendumvorlagenRepository;

    @Autowired
    private ReferendumvorlageRepository referendumvorlageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void tearDown() {
        referendumvorlageRepository.deleteAll();
        referendumvorlagenRepository.deleteAll();
    }

    @Nested
    class DeleteAllByBezirkUndWahlID_WahlID {

        @Test
        void should_removeReferendumvorlagenAndChildren_when_wahlIdMatches() {
            val wahlIDToDelete = "wahlID";

            val referendumvorlagenToKeep = transactionTemplate.execute(status -> {
                referendumvorlagenRepository.save(createReferendumvorlagenWithBezirkAndWahlID("wahlbezirk1", wahlIDToDelete));
                referendumvorlagenRepository.save(createReferendumvorlagenWithBezirkAndWahlID("wahlbezirk2", wahlIDToDelete));

                return referendumvorlagenRepository.save(createReferendumvorlagenWithBezirkAndWahlID("wahlbezirk1", "wahlIDToKeep"));
            });

            transactionTemplate.executeWithoutResult(status -> referendumvorlagenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlIDToDelete));

            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(1);
            Assertions.assertThat(referendumvorlagenRepository.existsById(referendumvorlagenToKeep.getId())).isTrue();

            Assertions.assertThat(referendumvorlageRepository.count()).isEqualTo(referendumvorlagenToKeep.getReferendumvorlagen().size());
            referendumvorlagenToKeep.getReferendumvorlagen()
                    .forEach(referendumvorlage -> Assertions.assertThat(referendumvorlageRepository.existsById(referendumvorlage.getId())).isTrue());
        }

        private Referendumvorlagen createReferendumvorlagenWithBezirkAndWahlID(final String wahlbezirkID, final String wahlID) {
            val referendumvorlagen = new Referendumvorlagen();

            referendumvorlagen.setBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID));
            referendumvorlagen.setStimmzettelgebietID(wahlID);

            referendumvorlagen.setReferendumvorlagen(new HashSet<>());
            val referendumvorlage1 = new Referendumvorlage(null, null, UUID.randomUUID().toString(), 1L, "", "", new HashSet<>());
            referendumvorlagen.addReferendumvorlage(referendumvorlage1);
            val referendumvorlage2 = new Referendumvorlage(null, null, UUID.randomUUID().toString(), 1L, "", "", new HashSet<>());
            referendumvorlagen.addReferendumvorlage(referendumvorlage2);

            val referendumoption1Vorlage1 = new Referendumoption(UUID.randomUUID().toString(), "", null);
            referendumvorlage1.getReferendumoptionen().add(referendumoption1Vorlage1);
            val referendumoption2Vorlage1 = new Referendumoption(UUID.randomUUID().toString(), "", null);
            referendumvorlage1.getReferendumoptionen().add(referendumoption2Vorlage1);
            val referendumoption1Vorlage2 = new Referendumoption(UUID.randomUUID().toString(), "", null);
            referendumvorlage2.getReferendumoptionen().add(referendumoption1Vorlage2);
            val referendumoption2Vorlage2 = new Referendumoption(UUID.randomUUID().toString(), "", null);
            referendumvorlage2.getReferendumoptionen().add(referendumoption2Vorlage2);

            return referendumvorlagen;
        }
    }
}
