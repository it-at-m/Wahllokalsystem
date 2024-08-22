package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
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
class WahlvorschlaegeRepositoryTest {

    @Autowired
    private WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @Autowired
    private WahlvorschlagRepository wahlvorschlagRepository;

    @Autowired
    private KandidatRepository kandidatRepository;

    @AfterEach
    void tearDown() {
        kandidatRepository.deleteAll();
        wahlvorschlagRepository.deleteAll();
        wahlvorschlaegeRepository.deleteAll();
    }

    @Test
    @Transactional
    public void wahlvorschlaegeRepositorySave() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val wahlvorschlaegeEntity = createWahlvorschlaegeEntity(wahlID, wahlbezirkID);
        val bezirkUndWahlID = wahlvorschlaegeEntity.getBezirkUndWahlID();

        wahlvorschlaegeRepository.save(wahlvorschlaegeEntity);

        Optional<Wahlvorschlaege> persistedWahlvorschlaege = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID);

        Assertions.assertThat(wahlvorschlaegeEntity).isEqualTo(persistedWahlvorschlaege.get());
    }

    @Test
    @Transactional
    public void savingParentWahlvorschlaegeIsAutomaticallySavingWahlvorschlagChildsAndHerKandidatChilds() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val wahlvorschlaegeEntity = createWahlvorschlaegeEntity(wahlID, wahlbezirkID);

        val expectedWahlvorschlagChilds = wahlvorschlaegeEntity.getWahlvorschlaege();

        Set<Kandidat> expectedAllKandidatChilds = new HashSet<>();
        for (Wahlvorschlag expectedWahlvorschlagChild : expectedWahlvorschlagChilds) {
            expectedAllKandidatChilds.addAll(expectedWahlvorschlagChild.getKandidaten());
        }

        wahlvorschlaegeRepository.save(wahlvorschlaegeEntity);

        val foundWahlvorschlagChilds = wahlvorschlagRepository.findAll();
        val foundKandidatChildsOfAllWahlvorschlags = kandidatRepository.findAll();

        Assertions.assertThat(foundWahlvorschlagChilds).containsExactlyInAnyOrderElementsOf(expectedWahlvorschlagChilds);
        Assertions.assertThat(foundKandidatChildsOfAllWahlvorschlags).containsExactlyInAnyOrderElementsOf(expectedAllKandidatChilds);
    }

    @Test
    @Transactional
    public void method_deleteAllByBezirkUndWahlID_WahlID_doesNotDeleteElementsWithOtherWahlIDButSameWahlbezirkID() {
        Assertions.assertThat(wahlvorschlaegeRepository.findAll()).isEmpty();
        Assertions.assertThat(wahlvorschlagRepository.findAll()).isEmpty();
        Assertions.assertThat(kandidatRepository.findAll()).isEmpty();

        val wahlID_1 = "wahlID_1";
        val wahlbezirkID = "wahlbezirkID";
        val wahlID_2 = "wahlID_2";

        val wahlvorschlaegeEntity1 = createWahlvorschlaegeEntity(wahlID_1, wahlbezirkID);
        val bezirkUndWahlID1 = wahlvorschlaegeEntity1.getBezirkUndWahlID();
        val wahlvorschlaegeEntity2 = createWahlvorschlaegeEntity(wahlID_2, wahlbezirkID);
        val bezirkUndWahlID2 = wahlvorschlaegeEntity2.getBezirkUndWahlID();

        wahlvorschlaegeRepository.save(wahlvorschlaegeEntity1);
        wahlvorschlaegeRepository.save(wahlvorschlaegeEntity2);

        val expectedWahlvorschlagChilds1 = wahlvorschlaegeEntity1.getWahlvorschlaege();
        Set<Kandidat> expectedAllKandidatChilds1 = new HashSet<>();
        for (Wahlvorschlag expectedWahlvorschlagChild : expectedWahlvorschlagChilds1) {
            expectedAllKandidatChilds1.addAll(expectedWahlvorschlagChild.getKandidaten());
        }

        val foundWahlvorschlaege_OfWahlID1 = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID1);
        val foundWahlvorschlagChilds_Of_wahlID1 = foundWahlvorschlaege_OfWahlID1.get().getWahlvorschlaege();
        val foundKandidatChildsOfAllWahlvorschlags_Of_wahlID1 = foundWahlvorschlaege_OfWahlID1.get().getWahlvorschlaege().stream().flatMap( wvorschlag -> wvorschlag.getKandidaten().stream());


        Assertions.assertThat(foundWahlvorschlagChilds_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedWahlvorschlagChilds1);
        Assertions.assertThat(foundKandidatChildsOfAllWahlvorschlags_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedAllKandidatChilds1);

        val expectedWahlvorschlagChilds2 = wahlvorschlaegeEntity2.getWahlvorschlaege();
        Set<Kandidat> expectedAllKandidatChilds2 = new HashSet<>();
        for (Wahlvorschlag expectedWahlvorschlagChild : expectedWahlvorschlagChilds2) {
            expectedAllKandidatChilds2.addAll(expectedWahlvorschlagChild.getKandidaten());
        }

        val foundWahlvorschlaege_OfWahlID2 = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID2);
        val foundWahlvorschlagChilds_Of_wahlID2 = foundWahlvorschlaege_OfWahlID2.get().getWahlvorschlaege();
        val foundKandidatChildsOfAllWahlvorschlags_Of_wahlID2 = foundWahlvorschlaege_OfWahlID2.get().getWahlvorschlaege().stream().flatMap( wvorschlag -> wvorschlag.getKandidaten().stream()).toList();

        Assertions.assertThat(foundWahlvorschlagChilds_Of_wahlID2).containsExactlyInAnyOrderElementsOf(expectedWahlvorschlagChilds2);
        Assertions.assertThat(foundKandidatChildsOfAllWahlvorschlags_Of_wahlID2).containsExactlyInAnyOrderElementsOf(expectedAllKandidatChilds2);

        wahlvorschlaegeRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID_1);

        val allRemainingWahlvorschlaege = wahlvorschlaegeRepository.findAll();
        val allRemainingWahlvorschlag = wahlvorschlagRepository.findAll();
        val allRemainingKandidat = kandidatRepository.findAll();

        Assertions.assertThat(allRemainingWahlvorschlaege).containsExactlyInAnyOrderElementsOf(foundWahlvorschlaege_OfWahlID2.stream().toList());
        Assertions.assertThat(allRemainingWahlvorschlag).containsExactlyInAnyOrderElementsOf(foundWahlvorschlagChilds_Of_wahlID2);
        Assertions.assertThat(allRemainingKandidat).containsExactlyInAnyOrderElementsOf(foundKandidatChildsOfAllWahlvorschlags_Of_wahlID2);

    }

    private Wahlvorschlaege createWahlvorschlaegeEntity(final String wahlID, final String wahlbezirkID) {
        val entity = new Wahlvorschlaege(null, new BezirkUndWahlID(wahlID, wahlbezirkID), "stimmzettelgebietID",
                null);
        val wahlvorschlag1 = new Wahlvorschlag(null, "id1" + wahlID + wahlbezirkID, entity, 1L, "kurzname1", true, null);
        val kandidat1 = new Kandidat(null, "kandidatID1" + wahlID + wahlbezirkID, wahlvorschlag1, "name1", 1L, true, 1L, true);
        val kandidat2 = new Kandidat(null, "kandidatID2" + wahlID + wahlbezirkID, wahlvorschlag1, "name2", 2L, false, 2L, false);
        wahlvorschlag1.setKandidaten(Set.of(kandidat1, kandidat2));

        val wahlvorschlag2 = new Wahlvorschlag(null, "id2" + wahlID + wahlbezirkID, entity, 2L, "kurzname2", false, null);
        val kandidat3 = new Kandidat(null, "kandidatID3" + wahlID + wahlbezirkID, wahlvorschlag2, "name3", 1L, true, 1L, true);
        val kandidat4 = new Kandidat(null, "kandidatID4" + wahlID + wahlbezirkID, wahlvorschlag2, "name4", 2L, false, 2L, false);
        wahlvorschlag2.setKandidaten(Set.of(kandidat3, kandidat4));

        entity.setWahlvorschlaege(Set.of(wahlvorschlag1, wahlvorschlag2));

        return entity;
    }
}
