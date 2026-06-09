package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicherStimmzettel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BedenklicheStimmzettelModelMapperTest {

  BedenklicheStimmzettelModelMapper unitUnderTest =
      Mappers.getMapper(BedenklicheStimmzettelModelMapper.class);

  @Nested
  class ToEntity {

    @Test
    void should_createEntity_when_modelIsGiven() {
      //            val modelToMap = new BedenklicherStimmzettelModel(12,
      // Set.of(SupplementModel.TOO_MANY_LISTENKREUZE,
      // SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES), ValidityModel.PARTIAL_VALID);
      //            val wahlbezirkID = "wahlbezirkID";
      //            val wahlID = "wahlID";
      //
      //            val result = unitUnderTest.toEntity(modelToMap, wahlbezirkID, wahlID);
      //
      //            val expectedResult = new BedenklicheStimmzettel(new
      // BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, modelToMap.orderIndex()),
      // Set.of(Supplement.TOO_MANY_LISTENKREUZE, Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
      // Validity.PARTIAL_VALID);
      //            Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnNull_when_nullIsGiven() {
      val result = unitUnderTest.toEntity(null, null, null);
      Assertions.assertThat(result).isEqualTo(null);
    }
  }

  @Nested
  class ToModel {

    @Nested
    class OfBedenklicherStimmzettel {

      @Test
      void should_createModel_when_entityIsGiven() {
        //            val wahlbezirkID = "wahlbezirkID";
        //            val wahlID = "wahlID";
        //            val entityToMap = new BedenklicheStimmzettel(new
        // BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 12),
        // Set.of(Supplement.TOO_MANY_LISTENKREUZE, Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
        // Validity.PARTIAL_VALID);
        //
        //            val result = unitUnderTest.toModel(entityToMap);
        //
        //            val expectedResult = new BedenklicherStimmzettelModel(12,
        // Set.of(SupplementModel.TOO_MANY_LISTENKREUZE,
        // SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES), ValidityModel.PARTIAL_VALID);
        //            Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnNull_when_nullIsGiven() {
        val result = unitUnderTest.toModel((BedenklicherStimmzettel) null);
        Assertions.assertThat(result).isEqualTo(null);
      }
    }
  }
}
