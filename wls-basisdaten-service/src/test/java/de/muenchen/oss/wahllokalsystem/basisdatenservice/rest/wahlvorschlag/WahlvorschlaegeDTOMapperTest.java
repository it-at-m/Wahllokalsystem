package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorschlaegeDTOMapperTest {

    private final WahlvorschlaegeDTOMapper unitUnderTest = Mappers.getMapper(WahlvorschlaegeDTOMapper.class);

    @Nested
    class FromWahlvorschlagModelToWLSDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val modelInput = createWahlvorschlaegeModel(wahlID, wahlbezirkID);
            val dtoExpected = createWahlvorschlaegeDTO(wahlID, wahlbezirkID);

            val result = unitUnderTest.toDTO(modelInput);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }

        private WahlvorschlaegeDTO createWahlvorschlaegeDTO(final String wahlID, final String wahlbezirkID) {
            return new WahlvorschlaegeDTO(wahlID, wahlbezirkID, "stimmzettelgebietID",
                    Set.of(
                            new WahlvorschlagDTO("id1", 1L, "kurzname1", true, Set.of(
                                    new KandidatDTO("kandidatID1", "name1", 1L, true, 1L, true),
                                    new KandidatDTO("kandidatID2", "name2", 2L, false, 2L, false))),
                            new WahlvorschlagDTO("id2", 2L, "kurzname2", false, Set.of(
                                    new KandidatDTO("kandidatID3", "name3", 1L, true, 1L, true),
                                    new KandidatDTO("kandidatID4", "name4", 2L, false, 2L, false)))));
        }

        private WahlvorschlaegeModel createWahlvorschlaegeModel(final String wahlID, final String wahlbezirkID) {
            return new WahlvorschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), "stimmzettelgebietID",
                    Set.of(
                            new WahlvorschlagModel("id1", 1L, "kurzname1", true, Set.of(
                                    new KandidatModel("kandidatID1", "name1", 1L, true, 1L, true),
                                    new KandidatModel("kandidatID2", "name2", 2L, false, 2L, false))),
                            new WahlvorschlagModel("id2", 2L, "kurzname2", false, Set.of(
                                    new KandidatModel("kandidatID3", "name3", 1L, true, 1L, true),
                                    new KandidatModel("kandidatID4", "name4", 2L, false, 2L, false)))));
        }
    }
}
