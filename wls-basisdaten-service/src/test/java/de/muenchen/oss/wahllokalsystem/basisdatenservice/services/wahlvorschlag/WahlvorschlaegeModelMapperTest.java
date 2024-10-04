package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorschlaegeModelMapperTest {

    private final WahlvorschlaegeModelMapper unitUnderTest = Mappers.getMapper(WahlvorschlaegeModelMapper.class);

    @Test
    void modelIsMappedToEntity() {
        val modelToMap = createWahlvorschlaegeModel();

        val result = unitUnderTest.toEntity(modelToMap);

        val expectedResult = createWahlvorschlaegeEntity();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void entityIsMappedToModel() {
        val entityToMap = createWahlvorschlaegeEntity();

        val result = unitUnderTest.toModel(entityToMap);
        val expectedResult = createWahlvorschlaegeModel();

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    private Wahlvorschlaege createWahlvorschlaegeEntity() {
        val entity = new Wahlvorschlaege(null, new BezirkUndWahlID("wahlID", "wahlbezirkID"), "stimmzettelgebietID",
                null);
        val wahlvorschlag1 = new Wahlvorschlag(null, "id1", entity, 1L, "kurzname1", true, null);
        val kandidat1 = new Kandidat(null, "kandidatID1", wahlvorschlag1, "name1", 1L, true, 1L, true);
        val kandidat2 = new Kandidat(null, "kandidatID2", wahlvorschlag1, "name2", 2L, false, 2L, false);
        wahlvorschlag1.setKandidaten(Set.of(kandidat1, kandidat2));

        val wahlvorschlag2 = new Wahlvorschlag(null, "id2", entity, 2L, "kurzname2", false, null);
        val kandidat3 = new Kandidat(null, "kandidatID3", wahlvorschlag2, "name3", 1L, true, 1L, true);
        val kandidat4 = new Kandidat(null, "kandidatID4", wahlvorschlag2, "name4", 2L, false, 2L, false);
        wahlvorschlag2.setKandidaten(Set.of(kandidat3, kandidat4));

        entity.setWahlvorschlaege(Set.of(wahlvorschlag1, wahlvorschlag2));

        return entity;
    }

    private WahlvorschlaegeModel createWahlvorschlaegeModel() {
        return new WahlvorschlaegeModel(new BezirkUndWahlID("wahlID", "wahlbezirkID"), "stimmzettelgebietID",
                Set.of(
                        new WahlvorschlagModel("id1", 1L, "kurzname1", true, Set.of(
                                new KandidatModel("kandidatID1", "name1", 1L, true, 1L, true),
                                new KandidatModel("kandidatID2", "name2", 2L, false, 2L, false))),
                        new WahlvorschlagModel("id2", 2L, "kurzname2", false, Set.of(
                                new KandidatModel("kandidatID3", "name3", 1L, true, 1L, true),
                                new KandidatModel("kandidatID4", "name4", 2L, false, 2L, false)))));
    }

}
