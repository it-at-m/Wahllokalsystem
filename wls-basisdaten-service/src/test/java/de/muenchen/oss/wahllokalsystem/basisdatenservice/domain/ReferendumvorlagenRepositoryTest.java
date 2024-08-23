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
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @AfterEach
    void tearDown() {
        referendumvorlageRepository.deleteAll();
        referendumvorlagenRepository.deleteAll();
    }

    @Test
    @Transactional
    public void referendumvorlagenRepositorySave() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val referendumvorlagenEntity = createReferendumvorlagenEntity(wahlID, wahlbezirkID);
        val bezirkUndWahlID = referendumvorlagenEntity.getBezirkUndWahlID();

        referendumvorlagenRepository.save(referendumvorlagenEntity);

        Optional<Referendumvorlagen> persistedReferendumvorlagen = referendumvorlagenRepository.findByBezirkUndWahlID(bezirkUndWahlID);

        Assertions.assertThat(referendumvorlagenEntity).isEqualTo(persistedReferendumvorlagen.get());
    }

    @Test
    @Transactional
    public void savingParentReferendumvorlagenIsAutomaticallySavingReferendumvorlageChilds() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val referendumvorlagenEntity = createReferendumvorlagenEntity(wahlID, wahlbezirkID);

        val expectedReferendumvorlageChilds = referendumvorlagenEntity.getReferendumvorlagen();

        Set<Referendumoption> expectedAllReferendumoptionChilds = new HashSet<>();
        for (Referendumvorlage expectedReferendumvorlageChild : expectedReferendumvorlageChilds) {
            expectedAllReferendumoptionChilds.addAll(expectedReferendumvorlageChild.getReferendumoptionen());
        }

        referendumvorlagenRepository.save(referendumvorlagenEntity);

        val foundReferendumvorlageChilds = referendumvorlageRepository.findAll();
        val foundReferendumoptionChildsOfAllReferendumvorlages = foundReferendumvorlageChilds.stream().flatMap( rvorlage -> rvorlage.getReferendumoptionen().stream()).toList();

        Assertions.assertThat(foundReferendumvorlageChilds).containsExactlyInAnyOrderElementsOf(expectedReferendumvorlageChilds);
        Assertions.assertThat(foundReferendumoptionChildsOfAllReferendumvorlages).containsExactlyInAnyOrderElementsOf(expectedAllReferendumoptionChilds);
    }

    @Test
    @Transactional
    public void method_deleteAllByBezirkUndWahlID_WahlID_doesNotDeleteElementsWithOtherWahlIDButSameWahlbezirkID() {
        Assertions.assertThat(referendumvorlagenRepository.findAll()).isEmpty();
        Assertions.assertThat(referendumvorlageRepository.findAll()).isEmpty();

        val wahlID_1 = "wahlID_1";
        val wahlbezirkID = "wahlbezirkID";
        val wahlID_2 = "wahlID_2";

        val referendumvorlagenEntity1 = createReferendumvorlagenEntity(wahlID_1, wahlbezirkID);
        val bezirkUndWahlID1 = referendumvorlagenEntity1.getBezirkUndWahlID();
        val referendumvorlagenEntity2 = createReferendumvorlagenEntity(wahlID_2, wahlbezirkID);
        val bezirkUndWahlID2 = referendumvorlagenEntity2.getBezirkUndWahlID();

        referendumvorlagenRepository.save(referendumvorlagenEntity1);
        referendumvorlagenRepository.save(referendumvorlagenEntity2);

        val expectedReferendumvorlageChilds1 = referendumvorlagenEntity1.getReferendumvorlagen();
        Set<Referendumoption> expectedAllReferendumoptionChilds1 = new HashSet<>();
        for (Referendumvorlage expectedReferendumvorlageChild : expectedReferendumvorlageChilds1) {
            expectedAllReferendumoptionChilds1.addAll(expectedReferendumvorlageChild.getReferendumoptionen());
        }

        val foundReferendumvorlagen_OfWahlID1 = referendumvorlagenRepository.findByBezirkUndWahlID(bezirkUndWahlID1);
        val foundReferendumvorlageChilds_Of_wahlID1 = foundReferendumvorlagen_OfWahlID1.get().getReferendumvorlagen();
        val foundReferendumoptionChildsOfAllReferendumvorlages_Of_wahlID1 = foundReferendumvorlagen_OfWahlID1.get().getReferendumvorlagen().stream().flatMap( rvorlage -> rvorlage.getReferendumoptionen().stream());

        Assertions.assertThat(foundReferendumvorlageChilds_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedReferendumvorlageChilds1);
        Assertions.assertThat(foundReferendumoptionChildsOfAllReferendumvorlages_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedAllReferendumoptionChilds1);

        val expectedReferendumvorlageChilds2 = referendumvorlagenEntity2.getReferendumvorlagen();
        Set<Referendumoption> expectedAllReferendumoptionChilds2 = new HashSet<>();
        for (Referendumvorlage expectedReferendumvorlageChild : expectedReferendumvorlageChilds2) {
            expectedAllReferendumoptionChilds2.addAll(expectedReferendumvorlageChild.getReferendumoptionen());
        }

        val foundReferendumvorlagen_OfWahlID2 = referendumvorlagenRepository.findByBezirkUndWahlID(bezirkUndWahlID2);
        val foundReferendumvorlageChilds_Of_wahlID2 = foundReferendumvorlagen_OfWahlID2.get().getReferendumvorlagen();
        val foundReferendumoptionChildsOfAllReferendumvorlages_Of_wahlID2 = foundReferendumvorlagen_OfWahlID2.get().getReferendumvorlagen().stream().flatMap( rvorlage -> rvorlage.getReferendumoptionen().stream()).toList();

        Assertions.assertThat(foundReferendumvorlageChilds_Of_wahlID2).containsExactlyInAnyOrderElementsOf(expectedReferendumvorlageChilds2);
        Assertions.assertThat(foundReferendumoptionChildsOfAllReferendumvorlages_Of_wahlID2).containsExactlyInAnyOrderElementsOf(expectedAllReferendumoptionChilds2);

        referendumvorlagenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID_1);

        val allRemainingReferendumvorlagen = referendumvorlagenRepository.findAll();
        val allRemainingReferendumvorlages = referendumvorlageRepository.findAll();
        val allRemainingReferendumoption = allRemainingReferendumvorlages.stream().flatMap(rvorlage -> rvorlage.getReferendumoptionen().stream()).toList();

        Assertions.assertThat(allRemainingReferendumvorlagen).containsExactlyInAnyOrderElementsOf(foundReferendumvorlagen_OfWahlID2.stream().toList());
        Assertions.assertThat(allRemainingReferendumvorlages).containsExactlyInAnyOrderElementsOf(foundReferendumvorlageChilds_Of_wahlID2);
        Assertions.assertThat(allRemainingReferendumoption).containsExactlyInAnyOrderElementsOf(foundReferendumoptionChildsOfAllReferendumvorlages_Of_wahlID2);
    }

    private Referendumvorlagen createReferendumvorlagenEntity(final String wahlID, final String wahlbezirkID) {
        val stimmzettelgebietID = "stimmzettelgebietID";
        val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        val entity = new Referendumvorlagen(null, bezirkUndWahlID, stimmzettelgebietID, null);
        val referendumvorlage_1 = new Referendumvorlage(null, entity, "wahlvorschlagID1", 1L, "kurzname1", "frage1",
                Set.of(new Referendumoption("option11" + wahlID + wahlbezirkID, "optionsName11", 1L), new Referendumoption("option12" + wahlID + wahlbezirkID, "optionsName12", 2L)));
        val referendumvorlage_2 = new Referendumvorlage(null, entity, "wahlvorschlagID2", 2L, "kurzname2", "frage2",
                Set.of(new Referendumoption("option21" + wahlID + wahlbezirkID, "optionsName21", 3L), new Referendumoption("option22" + wahlID + wahlbezirkID, "optionsName22", 4L)));
        entity.setReferendumvorlagen(Set.of(referendumvorlage_1, referendumvorlage_2));
        return entity;
    }
}
