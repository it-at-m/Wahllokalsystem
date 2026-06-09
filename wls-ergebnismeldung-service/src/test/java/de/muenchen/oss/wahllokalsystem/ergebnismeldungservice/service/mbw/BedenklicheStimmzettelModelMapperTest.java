package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicherStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BezirkIdWahlIdOrderIndex;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.Supplement;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.Validity;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

    @Nested
    class OfBedenklicherStimmzettel {

      @Test
      void should_createEntity_when_modelIsGiven() {
        val modelToMap =
            new BedenklicherStimmzettelModel(
                12,
                Set.of(
                    SupplementModel.TOO_MANY_LISTENKREUZE,
                    SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                ValidityModel.PARTIAL_VALID);
        val wahlbezirkID = "wahlbezirkID";
        val wahlID = "wahlID";

        val result = unitUnderTest.toEntity(modelToMap, wahlbezirkID, wahlID);

        val expectedResult =
            new BedenklicherStimmzettel(
                new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, modelToMap.orderIndex()),
                Set.of(Supplement.TOO_MANY_LISTENKREUZE, Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                null,
                Validity.PARTIAL_VALID);
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnNull_when_nullIsGiven() {
        val result = unitUnderTest.toEntity((BedenklicherStimmzettelModel) null, null, null);
        Assertions.assertThat(result).isEqualTo(null);
      }
    }

    @Nested
    class OfCollectionOfBedenklicheStimmzettel {

      @Test
      void should_createEntity_when_modelIsGiven() {
        val modelToMap =
            List.of(
                new BedenklicherStimmzettelModel(
                    12,
                    Set.of(
                        SupplementModel.TOO_MANY_LISTENKREUZE,
                        SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                    ValidityModel.PARTIAL_VALID),
                new BedenklicherStimmzettelModel(11, Collections.emptySet(), ValidityModel.VALID));
        val wahlbezirkID = "wahlbezirkID";
        val wahlID = "wahlID";

        val result = unitUnderTest.toEntity(modelToMap, wahlbezirkID, wahlID);

        val expectedListOfBedenklicheStimmzettel = new ArrayList<BedenklicherStimmzettel>();
        val expectedResult =
            new BedenklicheStimmzettel(
                new BezirkUndWahlID(wahlID, wahlbezirkID), expectedListOfBedenklicheStimmzettel);
        expectedResult
            .getBedenklicheStimmzettels()
            .add(
                new BedenklicherStimmzettel(
                    new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 12),
                    Set.of(
                        Supplement.TOO_MANY_LISTENKREUZE,
                        Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                    expectedResult,
                    Validity.PARTIAL_VALID));
        expectedResult
            .getBedenklicheStimmzettels()
            .add(
                new BedenklicherStimmzettel(
                    new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 11),
                    Collections.emptySet(),
                    expectedResult,
                    Validity.VALID));
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnDTOWithEmptyValues_when_emptyListIsGiven() {
        val result = unitUnderTest.toEntity(Collections.emptyList(), null, null);
        val expectedResult =
            new BedenklicheStimmzettel(new BezirkUndWahlID(null, null), Collections.emptyList());
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }
    }
  }

  @Nested
  class ToModel {

    @Nested
    class OfBedenklicherStimmzettel {

      @Test
      void should_createModel_when_entityIsGiven() {
        val wahlbezirkID = "wahlbezirkID";
        val wahlID = "wahlID";
        val entityToMap =
            new BedenklicherStimmzettel(
                new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 12),
                Set.of(Supplement.TOO_MANY_LISTENKREUZE, Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                new BedenklicheStimmzettel(),
                Validity.PARTIAL_VALID);

        val result = unitUnderTest.toModel(entityToMap);

        val expectedResult =
            new BedenklicherStimmzettelModel(
                12,
                Set.of(
                    SupplementModel.TOO_MANY_LISTENKREUZE,
                    SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                ValidityModel.PARTIAL_VALID);
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnNull_when_nullIsGiven() {
        val result = unitUnderTest.toModel((BedenklicherStimmzettel) null);
        Assertions.assertThat(result).isEqualTo(null);
      }
    }

    @Nested
    class OfCollectionOfBedenklicheStimmzettel {

      @Test
      void should_createModel_when_collectionOFentitiesAreGiven() {
        val wahlbezirkID = "wahlbezirkID";
        val wahlID = "wahlID";
        val entityToMap =
            List.of(
                new BedenklicherStimmzettel(
                    new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 11),
                    Set.of(
                        Supplement.TOO_MANY_LISTENKREUZE,
                        Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                    new BedenklicheStimmzettel(),
                    Validity.PARTIAL_VALID),
                new BedenklicherStimmzettel(
                    new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 12),
                    Collections.emptySet(),
                    new BedenklicheStimmzettel(),
                    Validity.VALID));

        val result = unitUnderTest.toModel(entityToMap);

        val expectedResult =
            List.of(
                new BedenklicherStimmzettelModel(
                    11,
                    Set.of(
                        SupplementModel.TOO_MANY_LISTENKREUZE,
                        SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                    ValidityModel.PARTIAL_VALID),
                new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.VALID));
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }
    }
  }
}
