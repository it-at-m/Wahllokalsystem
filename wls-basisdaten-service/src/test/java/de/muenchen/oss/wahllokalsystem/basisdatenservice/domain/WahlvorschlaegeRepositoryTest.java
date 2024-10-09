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
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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
                persistWahlvorschlaege(createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk1", wahlIDToDelete));
                persistWahlvorschlaege(createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk2", wahlIDToDelete));

                return persistWahlvorschlaege(createWahlvorschlaegeWithKandidatenWithBezirkAndWahlID("wahlbezirk1", "wahlIDToKeep"));
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

        private Wahlvorschlaege persistWahlvorschlaege(final Wahlvorschlaege wahlvorschlaegeToPersist) {
            val createdEntity = wahlvorschlaegeRepository.save(wahlvorschlaegeToPersist);
            wahlvorschlaegeToPersist.getWahlvorschlaege().forEach(wahlvorschlag -> {
                wahlvorschlagRepository.save(wahlvorschlag);
                kandidatRepository.saveAll(wahlvorschlag.getKandidaten());
            });

            return createdEntity;
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
        val foundKandidatChildsOfAllWahlvorschlags_Of_wahlID1 = foundWahlvorschlaege_OfWahlID1.get().getWahlvorschlaege().stream()
                .flatMap(wvorschlag -> wvorschlag.getKandidaten().stream());

        Assertions.assertThat(foundWahlvorschlagChilds_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedWahlvorschlagChilds1);
        Assertions.assertThat(foundKandidatChildsOfAllWahlvorschlags_Of_wahlID1).containsExactlyInAnyOrderElementsOf(expectedAllKandidatChilds1);

        val expectedWahlvorschlagChilds2 = wahlvorschlaegeEntity2.getWahlvorschlaege();
        Set<Kandidat> expectedAllKandidatChilds2 = new HashSet<>();
        for (Wahlvorschlag expectedWahlvorschlagChild : expectedWahlvorschlagChilds2) {
            expectedAllKandidatChilds2.addAll(expectedWahlvorschlagChild.getKandidaten());
        }

        val foundWahlvorschlaege_OfWahlID2 = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID2);
        val foundWahlvorschlagChilds_Of_wahlID2 = foundWahlvorschlaege_OfWahlID2.get().getWahlvorschlaege();
        val foundKandidatChildsOfAllWahlvorschlags_Of_wahlID2 = foundWahlvorschlaege_OfWahlID2.get().getWahlvorschlaege().stream()
                .flatMap(wvorschlag -> wvorschlag.getKandidaten().stream()).toList();

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
