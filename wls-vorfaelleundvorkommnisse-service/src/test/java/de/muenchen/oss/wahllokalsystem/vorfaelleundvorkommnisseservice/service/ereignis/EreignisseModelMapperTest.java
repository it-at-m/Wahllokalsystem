package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisse;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EreignisseModelMapperTest {

  private final EreignisseModelMapper unitUnderTest =
      Mappers.getMapper(EreignisseModelMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnEreignisseModel_when_givenEreignisse() {
      val wahlbezirkID = "wahlbezirkID";

      val ereignis1 = TestdataFactory.CreateEreignisEntity.withData("beschreibung1");
      val ereignis2 = TestdataFactory.CreateEreignisEntity.withData("beschreibung2");
      val ereignisEntitiesList = Set.of(ereignis1, ereignis2);
      val ereignisseEntity =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, ereignisEntitiesList);

      val expectedEreignisModel1 = TestdataFactory.CreateEreignisModel.fromEntity(ereignis1);
      val expectedEreignisModel2 = TestdataFactory.CreateEreignisModel.fromEntity(ereignis2);
      val ereignisModelList = List.of(expectedEreignisModel1, expectedEreignisModel2);
      val expectedWahlbezirkEreignisseModel =
          new EreignisseModel(wahlbezirkID, true, true, ereignisModelList);

      val result = unitUnderTest.toModel(ereignisseEntity);
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedWahlbezirkEreignisseModel);
    }
  }

  @Nested
  class ToEntity {

    @Test
    void should_returnEreignisse_when_givenEreignisseModel() {
      val wahlbezirkID = "wahlbezirkID";

      val ereignisModel1 = TestdataFactory.CreateEreignisModel.withData("beschreibung1");
      val ereignisModel2 = TestdataFactory.CreateEreignisModel.withData("beschreibung2");
      val ereignisModelList = List.of(ereignisModel1, ereignisModel2);
      val mockedEreignisseWriteModel =
          TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, ereignisModelList);

      val expectedEreignis1 = TestdataFactory.CreateEreignisEntity.fromModel(ereignisModel1);
      val expectedEreignis2 = TestdataFactory.CreateEreignisEntity.fromModel(ereignisModel2);
      val ereignisList = Set.of(expectedEreignis1, expectedEreignis2);
      val expectedEreignisse = new Ereignisse(wahlbezirkID, true, true, ereignisList);

      val result = unitUnderTest.toEntity(mockedEreignisseWriteModel);
      Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedEreignisse);
    }
  }
}
