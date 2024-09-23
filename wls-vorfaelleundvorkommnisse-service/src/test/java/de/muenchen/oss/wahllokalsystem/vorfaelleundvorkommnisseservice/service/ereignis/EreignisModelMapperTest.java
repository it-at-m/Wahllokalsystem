package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EreignisModelMapperTest {

    private final EreignisModelMapper unitUnderTest = Mappers.getMapper(EreignisModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnEreignisModel_when_givenEreignisEntity() {
            val mockedEreignisEntity = TestdataFactory.CreateEreignisEntity.withData("wahlbezirkID");
            val expectedEreignisModel = TestdataFactory.CreateEreignisModel.fromEntity(mockedEreignisEntity);

            val result = unitUnderTest.toModel(mockedEreignisEntity);
            Assertions.assertThat(result).isEqualTo(expectedEreignisModel);
        }

        @Test
        void should_returnEreignisseModel_when_givenWahlbezirkIDKeineVorfaelleKeineVorkommnisseAndListOfEreignisModel() {
            val wahlbezirkID = "wahlbezirkID";
            val keineVorfaelle = true;
            val keineVorkommnisse = false;

            val mockedListOfEreignisModel = List.of(
                    TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORFALL),
                    TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORKOMMNIS));
            val expectedEreignisseModel = TestdataFactory.CreateEreignisseModel.withData(wahlbezirkID, keineVorfaelle, keineVorkommnisse,
                    mockedListOfEreignisModel);

            val result = unitUnderTest.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, mockedListOfEreignisModel);
            Assertions.assertThat(result).isEqualTo(expectedEreignisseModel);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnEreignisEntity_when_givenWahlbezirkIDAndEreignisModel() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisModel = TestdataFactory.CreateEreignisModel.withData();
            val expectedEreignisEntity = TestdataFactory.CreateEreignisEntity.fromModel(mockedEreignisModel, wahlbezirkID);

            val result = unitUnderTest.toEntity(mockedEreignisModel, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(expectedEreignisEntity);
        }

        @Test
        void should_returnListOfEreignisEntities_when_givenEreignisseWriteModel() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisModel1 = TestdataFactory.CreateEreignisModel.withData();
            val mockedEreignisModel2 = TestdataFactory.CreateEreignisModel.withData();
            val mockedListOfEreignisModel = List.of(mockedEreignisModel1, mockedEreignisModel2);
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedListOfEreignisModel);
            val expectedListOfEreignis = List.of(
                    TestdataFactory.CreateEreignisEntity.fromModel(mockedEreignisModel1, wahlbezirkID),
                    TestdataFactory.CreateEreignisEntity.fromModel(mockedEreignisModel2, wahlbezirkID));

            val result = unitUnderTest.toEntity(mockedEreignisseWriteModel);
            Assertions.assertThat(result).isEqualTo(expectedListOfEreignis);
        }
    }

}
