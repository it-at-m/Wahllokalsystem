package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.apache.commons.collections.ArrayStack;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EreignisModelMapperTest {

    private final EreignisModelMapper unitUnderTest = Mappers.getMapper(EreignisModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void toModel() {
            val ereignisEntity = TestdataFactory.createEreignisEntityWithData("wahlbezirkID", "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val ereignisModelFromEntity = TestdataFactory.createEreignisModelFromEntity(ereignisEntity);

            val result = unitUnderTest.toModel(ereignisEntity);
            Assertions.assertThat(result).isEqualTo(ereignisModelFromEntity);
        }

        @Test
        void toEreignisseModel() {
            val wahlbezirkID = "wahlbezirkID";
            val keineVorfaelle = true;
            val keineVorkommnisse = false;
            List<EreignisModel> listOfEreignisModel = new ArrayList<>();
            listOfEreignisModel.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL));
            listOfEreignisModel.add(TestdataFactory.createEreignisModelWithData("beschreibung2", LocalDateTime.now(), Ereignisart.VORKOMMNIS));
            val ereignisseModel = TestdataFactory.createEreignisseModelWithData(wahlbezirkID, keineVorfaelle, keineVorkommnisse, listOfEreignisModel);

            val result = unitUnderTest.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, listOfEreignisModel);
            Assertions.assertThat(result).isEqualTo(ereignisseModel);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void toEntity() {
            val wahlbezirkID = "wahlbezirkID";
            val ereignisModel = TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val ereignisEntityFromModel = TestdataFactory.createEreignisEntityFromModel(ereignisModel, wahlbezirkID);

            val result = unitUnderTest.toEntity(ereignisModel, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(ereignisEntityFromModel);
        }

        @Test
        void toEntityList() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> listOfEreignisModel = new ArrayList<>();
            val model1 = TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val model2 = TestdataFactory.createEreignisModelWithData("beschreibung2", LocalDateTime.now(), Ereignisart.VORKOMMNIS);
            listOfEreignisModel.add(model1);
            listOfEreignisModel.add(model2);
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, listOfEreignisModel);
            List<Ereignis> listOfEreignis = new ArrayStack();
            listOfEreignis.add(TestdataFactory.createEreignisEntityFromModel(model1, wahlbezirkID));
            listOfEreignis.add(TestdataFactory.createEreignisEntityFromModel(model2, wahlbezirkID));

            val result = unitUnderTest.toEntity(ereignisseWriteModel);
            Assertions.assertThat(result).isEqualTo(listOfEreignis);
        }
    }

}
