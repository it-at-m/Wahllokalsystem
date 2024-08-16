package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumoption;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ReferendumvorlagenModelMapperTest {

    private final ReferendumvorlagenModelMapper unitUnderTest = Mappers.getMapper(ReferendumvorlagenModelMapper.class);

    @Nested
    class ToBezirkUndWahlID {

        @Test
        void isMapped() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val modelToMap = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);

            val result = unitUnderTest.toBezirkUndWahlID(modelToMap);

            val expectedResult = new BezirkUndWahlID(wahlID, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stimmzettelgebietID = "stimmzettelgebietID";

            val entityToMap = new Referendumvorlagen(UUID.randomUUID(), new BezirkUndWahlID(wahlID, wahlbezirkID), stimmzettelgebietID,
                    Set.of(new Referendumvorlage(null, null, "wahlvorschlagID1", 1L, "kurzname1", "frage1",
                            Set.of(new Referendumoption("option11", "optionsName11", 1L), new Referendumoption("option12", "optionsName12", 2L))),
                            new Referendumvorlage(null, null, "wahlvorschlagID2", 2L, "kurzname2", "frage2",
                                    Set.of(new Referendumoption("option21", "optionsName21", 3L), new Referendumoption("option22", "optionsName22", 4L)))));

            val result = unitUnderTest.toModel(entityToMap);

            val expectedResult = new ReferendumvorlagenModel(stimmzettelgebietID,
                    Set.of(new ReferendumvorlageModel("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                            Set.of(new ReferendumoptionModel("option11", "optionsName11", 1L),
                                    new ReferendumoptionModel("option12", "optionsName12", 2L))),
                            new ReferendumvorlageModel("wahlvorschlagID2", 2L, "kurzname2", "frage2",
                                    Set.of(new ReferendumoptionModel("option21", "optionsName21", 3L),
                                            new ReferendumoptionModel("option22", "optionsName22", 4L)))));
            Assertions.assertThat(result).isEqualTo(expectedResult);
            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    class ToEntity {

        @Nested
        class OfReferendumvorlagenModel {

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
                val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

                val result = unitUnderTest.toEntity(modelToMap, bezirkUndWahlID);

                val expectedResult = new Referendumvorlagen(null, bezirkUndWahlID, stimmzettelgebietID,
                        Set.of(new Referendumvorlage(null, null, "wahlvorschlagID1", 1L, "kurzname1", "frage1",
                                Set.of(new Referendumoption("option11", "optionsName11", 1L), new Referendumoption("option12", "optionsName12", 2L))),
                                new Referendumvorlage(null, null, "wahlvorschlagID2", 2L, "kurzname2", "frage2",
                                        Set.of(new Referendumoption("option21", "optionsName21", 3L), new Referendumoption("option22", "optionsName22", 4L)))));
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }

        @Nested
        class OfReferendumvorlageModel {

            @Test
            void isMapped() {
                val modelToMap = new ReferendumvorlageModel("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                        Set.of(new ReferendumoptionModel("option11", "optionsName11", 1L),
                                new ReferendumoptionModel("option12", "optionsName12", 2L)));

                val result = unitUnderTest.toEntity(modelToMap);

                val expectedResult = new Referendumvorlage(null, null, "wahlvorschlagID1", 1L, "kurzname1", "frage1",
                        Set.of(new Referendumoption("option11", "optionsName11", 1L), new Referendumoption("option12", "optionsName12", 2L)));
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }
    }

}
