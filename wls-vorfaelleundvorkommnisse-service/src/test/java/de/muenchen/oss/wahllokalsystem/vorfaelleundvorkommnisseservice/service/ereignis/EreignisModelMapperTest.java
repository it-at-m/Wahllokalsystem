package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.util.ArrayList;
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
        void should_return_EreignisModel_when_given_EreignisEntity() {
            val ereignisEntity = TestdataFactory.createEreignisEntityWithData("wahlbezirkID");
            val ereignisModelFromEntity = TestdataFactory.createEreignisModelFromEntity(ereignisEntity);

            val result = unitUnderTest.toModel(ereignisEntity);
            Assertions.assertThat(result).isEqualTo(ereignisModelFromEntity);
        }

        @Test
        void should_return_EreignisseModel_when_given_wahlbezirkID_keineVorfaelle_keineVorkommnisse_and_listOfEreignisModel() {
            val wahlbezirkID = "wahlbezirkID";
            val keineVorfaelle = true;
            val keineVorkommnisse = false;
            List<EreignisModel> listOfEreignisModel = new ArrayList<>();
            listOfEreignisModel.add(TestdataFactory.createEreignisModelWithEreignisart(Ereignisart.VORFALL));
            listOfEreignisModel.add(TestdataFactory.createEreignisModelWithEreignisart(Ereignisart.VORKOMMNIS));
            val ereignisseModel = TestdataFactory.createEreignisseModelWithData(wahlbezirkID, keineVorfaelle, keineVorkommnisse, listOfEreignisModel);

            val result = unitUnderTest.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, listOfEreignisModel);
            Assertions.assertThat(result).isEqualTo(ereignisseModel);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_return_EreignisEntity_when_given_wahlbezirkID_and_EreignisModel() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData();
            val ereignisEntityFromModel = TestdataFactory.createEreignisEntityFromModel(ereignisModel, wahlbezirkID);

            val result = unitUnderTest.toEntity(ereignisModel, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(ereignisEntityFromModel);
        }

        @Test
        void should_return_list_of_EreignisEntities_when_given_EreignisseWriteModel() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> listOfEreignisModel = new ArrayList<>();
            val model1 = TestdataFactory.createEreignisModelWithData();
            val model2 = TestdataFactory.createEreignisModelWithData();
            listOfEreignisModel.add(model1);
            listOfEreignisModel.add(model2);
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, listOfEreignisModel);
            List<Ereignis> listOfEreignis = new ArrayList<>();
            listOfEreignis.add(TestdataFactory.createEreignisEntityFromModel(model1, wahlbezirkID));
            listOfEreignis.add(TestdataFactory.createEreignisEntityFromModel(model2, wahlbezirkID));

            val result = unitUnderTest.toEntity(ereignisseWriteModel);
            Assertions.assertThat(result).isEqualTo(listOfEreignis);
        }
    }

}
