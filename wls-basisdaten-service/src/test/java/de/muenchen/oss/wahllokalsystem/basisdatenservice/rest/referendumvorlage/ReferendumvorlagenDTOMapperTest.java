package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumoptionModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlageModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenReferenceModel;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ReferendumvorlagenDTOMapperTest {

    private final ReferendumvorlagenDTOMapper unitUnderTest = Mappers.getMapper(ReferendumvorlagenDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val stimmzettelgebietID = "stimmzettelgebietID";

            val modelToMap = new ReferendumvorlagenModel(stimmzettelgebietID,
                    Set.of(new ReferendumvorlageModel("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                            Set.of(new ReferendumoptionModel("option11", "optionsName11", 1L),
                                    new ReferendumoptionModel("option12", "optionsName12", 2L))),
                            new ReferendumvorlageModel("wahlvorschlagID2", 2L, "kurzname2", "frage2",
                                    Set.of(new ReferendumoptionModel("option21", "optionsName21", 3L),
                                            new ReferendumoptionModel("option22", "optionsName22", 4L)))));

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new ReferendumvorlagenDTO(stimmzettelgebietID,
                    Set.of(new ReferendumvorlageDTO("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                            Set.of(new ReferendumoptionDTO("option11", "optionsName11", 1L),
                                    new ReferendumoptionDTO("option12", "optionsName12", 2L))),
                            new ReferendumvorlageDTO("wahlvorschlagID2", 2L, "kurzname2", "frage2",
                                    Set.of(new ReferendumoptionDTO("option21", "optionsName21", 3L),
                                            new ReferendumoptionDTO("option22", "optionsName22", 4L)))));
            Assertions.assertThat(result).isEqualTo(expectedResult);
            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            val result = unitUnderTest.toModel(wahlbezirkID, wahlID);

            val expectedResult = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

}
