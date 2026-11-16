package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingTest {

  @InjectMocks Mapping unitUnderTest;

  @Nested
  class ToClientDTO {

    @Test
    void should_mapToAWerteDTO_when_aWerteEntityIsGiven() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val a1 = 11L;
      val a2 = 22L;
      val entityToMap = new AWerte(new BezirkUndWahlID(wahlID, wahlbezirkID), a1, a2);

      val result = unitUnderTest.toClientDTO(entityToMap);

      val expectedResult = new AWerteDTO().a1(a1).a2(a2);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_createEmptyAWerteDTO_when_aWerteEntityIsNull() {
      val result = unitUnderTest.toClientDTO(null);

      Assertions.assertThat(result).isEqualTo(new AWerteDTO());
    }
  }

  @Nested
  class ToDtoErgebnisseSet {

    @Test
    void should_mapToSetWithDTO_when_listOfEntitiesIsGiven() {
      val entitiesToMap =
          List.of(
              new ErgebnisseModel(
                  "wbzID1",
                  "wahlID1",
                  StapelartModel.BTW_A,
                  List.of(
                      new ErgebnisModel("wahlvorschlag11", "kandidat11", 1L, 11, null),
                      new ErgebnisModel("wahlvorschlag12", "kandidat12", 2L, 12, null))),
              new ErgebnisseModel(
                  "wbzID2",
                  "wahlID2",
                  StapelartModel.EUW_C_GUELTIG,
                  List.of(
                      new ErgebnisModel("wahlvorschlag21", "kandidat21", 3L, 21, null),
                      new ErgebnisModel("wahlvorschlag22", "kandidat22", 4L, 22, null))));

      val result = unitUnderTest.toDtoErgebnisseSet(entitiesToMap);

      val expectedResult =
          Set.of(
              new ErgebnisDTO()
                  .stimmenart("BTW_A")
                  .ergebnis(11L)
                  .kandidatID("kandidat11")
                  .wahlvorschlagID("wahlvorschlag11")
                  .wahlvorschlagsordnungszahl(1L),
              new ErgebnisDTO()
                  .stimmenart("BTW_A")
                  .ergebnis(12L)
                  .kandidatID("kandidat12")
                  .wahlvorschlagID("wahlvorschlag12")
                  .wahlvorschlagsordnungszahl(2L),
              new ErgebnisDTO()
                  .stimmenart("EUW_C_GUELTIG")
                  .ergebnis(21L)
                  .kandidatID("kandidat21")
                  .wahlvorschlagID("wahlvorschlag21")
                  .wahlvorschlagsordnungszahl(3L),
              new ErgebnisDTO()
                  .stimmenart("EUW_C_GUELTIG")
                  .ergebnis(22L)
                  .kandidatID("kandidat22")
                  .wahlvorschlagID("wahlvorschlag22")
                  .wahlvorschlagsordnungszahl(4L));

      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10, 20, false",
      "10, 15, 25, false",
      "10, 10, 20, true",
      "10, 15, 25, true",
      "2147483648, 2147483648, 4294967296, false",
      "2147483648, 2147483648, 4294967296, true"
    })
    void should_sumResults_when_duplicateKeysAreGiven(
        final long firstCount,
        final long secondCount,
        final long expectedCount,
        final boolean acrossModels) {
      val first = new ErgebnisModel("wahlvorschlag", "kandidat", 1L, firstCount, 1L);
      val second = new ErgebnisModel("wahlvorschlag", "kandidat", 1L, secondCount, 2L);
      val entitiesToMap =
          acrossModels
              ? List.of(
                  new ErgebnisseModel("wbzID", "wahlID", StapelartModel.BTW_A, List.of(first)),
                  new ErgebnisseModel("wbzID", "wahlID", StapelartModel.BTW_A, List.of(second)))
              : List.of(
                  new ErgebnisseModel(
                      "wbzID", "wahlID", StapelartModel.BTW_A, List.of(first, second)));

      val result = unitUnderTest.toDtoErgebnisseSet(entitiesToMap);

      val expectedResult =
          new ErgebnisDTO()
              .stimmenart("BTW_A")
              .wahlvorschlagsordnungszahl(1L)
              .wahlvorschlagID("wahlvorschlag")
              .kandidatID("kandidat")
              .ergebnis(expectedCount);
      Assertions.assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
      "EUW_C_GUELTIG, 1, wahlvorschlag, kandidat",
      "BTW_A, 2, wahlvorschlag, kandidat",
      "BTW_A, 1, andererWahlvorschlag, kandidat",
      "BTW_A, 1, wahlvorschlag, andererKandidat"
    })
    void should_keepResultsSeparate_when_anyKeyFieldDiffers(
        final StapelartModel stapelart,
        final Long ordnungszahl,
        final String wahlvorschlagID,
        final String kandidatID) {
      val entitiesToMap =
          List.of(
              new ErgebnisseModel(
                  "wbzID",
                  "wahlID",
                  StapelartModel.BTW_A,
                  List.of(new ErgebnisModel("wahlvorschlag", "kandidat", 1L, 10, null))),
              new ErgebnisseModel(
                  "wbzID",
                  "wahlID",
                  stapelart,
                  List.of(new ErgebnisModel(wahlvorschlagID, kandidatID, ordnungszahl, 10, null))));

      val result = unitUnderTest.toDtoErgebnisseSet(entitiesToMap);

      Assertions.assertThat(result)
          .containsExactlyInAnyOrder(
              new ErgebnisDTO()
                  .stimmenart("BTW_A")
                  .wahlvorschlagsordnungszahl(1L)
                  .wahlvorschlagID("wahlvorschlag")
                  .kandidatID("kandidat")
                  .ergebnis(10L),
              new ErgebnisDTO()
                  .stimmenart(stapelart.name())
                  .wahlvorschlagsordnungszahl(ordnungszahl)
                  .wahlvorschlagID(wahlvorschlagID)
                  .kandidatID(kandidatID)
                  .ergebnis(10L));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1L, 1L})
    void should_sumResults_when_candidateIsNullAndOrdinalIsNullable(final Long ordnungszahl) {
      val entitiesToMap =
          List.of(
              new ErgebnisseModel(
                  "wbzID",
                  "wahlID",
                  StapelartModel.BTW_A,
                  List.of(
                      new ErgebnisModel("wahlvorschlag", null, ordnungszahl, 10, null),
                      new ErgebnisModel("wahlvorschlag", null, ordnungszahl, 15, null))));

      val result = unitUnderTest.toDtoErgebnisseSet(entitiesToMap);

      Assertions.assertThat(result)
          .containsExactly(
              new ErgebnisDTO()
                  .stimmenart("BTW_A")
                  .wahlvorschlagsordnungszahl(ordnungszahl)
                  .wahlvorschlagID("wahlvorschlag")
                  .kandidatID(null)
                  .ergebnis(25L));
    }

    @Test
    void should_returnEmptySet_when_collectionIsEmpty() {
      Assertions.assertThat(unitUnderTest.toDtoErgebnisseSet(List.of())).isEmpty();
    }

    @Test
    void should_returnEmptySet_when_resultListIsEmpty() {
      val entitiesToMap =
          List.of(new ErgebnisseModel("wbzID", "wahlID", StapelartModel.BTW_A, List.of()));

      Assertions.assertThat(unitUnderTest.toDtoErgebnisseSet(entitiesToMap)).isEmpty();
    }

    @Test
    void should_throwArithmeticException_when_sumOverflows() {
      val entitiesToMap =
          List.of(
              new ErgebnisseModel(
                  "wbzID",
                  "wahlID",
                  StapelartModel.BTW_A,
                  List.of(
                      new ErgebnisModel("wahlvorschlag", "kandidat", 1L, Long.MAX_VALUE, null),
                      new ErgebnisModel("wahlvorschlag", "kandidat", 1L, 1, null))));

      Assertions.assertThatThrownBy(() -> unitUnderTest.toDtoErgebnisseSet(entitiesToMap))
          .isInstanceOf(ArithmeticException.class);
    }
  }

  @Nested
  class ToWahlartDTO {

    @ParameterizedTest
    @EnumSource(WahlartModel.class)
    void should_returnWahlartEnumWithSameName_when_wahlartModelIsGiven(
        final WahlartModel wahlartModel) {
      Assertions.assertThat(unitUnderTest.toWahlartDTO(wahlartModel).name())
          .isEqualTo(wahlartModel.name());
    }
  }

  @Nested
  class ToDtoSet {

    @Test
    void should_returnSetWithUngueltigeStimmzettelDTO_when_listWithErgebnisseIsGiven() {
      val listWithErgebnisse =
          List.of(
              new ErgebnisseModel(
                  "wbzID1",
                  "wahlID1",
                  StapelartModel.BTW_A,
                  List.of(
                      new ErgebnisModel("wahlvorschlag11", null, null, 11, null),
                      new ErgebnisModel("wahlvorschlag12", null, null, 12, null))),
              new ErgebnisseModel(
                  "wbzID2",
                  "wahlID2",
                  StapelartModel.EUW_C_GUELTIG,
                  List.of(
                      new ErgebnisModel("wahlvorschlag21", null, null, 21, null),
                      new ErgebnisModel("wahlvorschlag22", null, null, 22, null))));

      val result = unitUnderTest.toDtoSet(listWithErgebnisse);

      val expectedResult =
          Set.of(
              new UngueltigeStimmzettelDTO()
                  .wahlvorschlagID("wahlvorschlag11")
                  .anzahl(11L)
                  .stimmenart("BTW_A"),
              new UngueltigeStimmzettelDTO()
                  .wahlvorschlagID("wahlvorschlag12")
                  .anzahl(12L)
                  .stimmenart("BTW_A"),
              new UngueltigeStimmzettelDTO()
                  .wahlvorschlagID("wahlvorschlag21")
                  .anzahl(21L)
                  .stimmenart("EUW_C_GUELTIG"),
              new UngueltigeStimmzettelDTO()
                  .wahlvorschlagID("wahlvorschlag22")
                  .anzahl(22L)
                  .stimmenart("EUW_C_GUELTIG"));

      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(StapelartModel.class)
    void should_mapToStimmenart_when_anyStapelIsGiven(final StapelartModel stapelart) {
      val listWithErgebnisse =
          List.of(
              new ErgebnisseModel(
                  "wbzID1",
                  "wahlID1",
                  stapelart,
                  List.of(new ErgebnisModel("wahlvorschlag11", null, null, 11, null))));

      val result = unitUnderTest.toDtoSet(listWithErgebnisse);

      val expectedResult =
          Set.of(
              new UngueltigeStimmzettelDTO()
                  .wahlvorschlagID("wahlvorschlag11")
                  .anzahl(11L)
                  .stimmenart(stapelart.name()));

      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }
  }

  @Nested
  class ToDTO {

    @ParameterizedTest(name = "given: {0} - expected: {1}")
    @MethodSource("argumentsWithGivenAndExpectedValues")
    void should_returnMeldungsartEnum_when_meldungsartModelIsGiven(
        final ArgumentsAccessor arguments) {
      val result = unitUnderTest.toDTO(arguments.get(0, MeldungsartModel.class));

      val expectedResult = arguments.get(1, ErgebnismeldungDTO.MeldungsartEnum.class);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    public static Stream<Arguments> argumentsWithGivenAndExpectedValues() {
      return Stream.of(
          Arguments.of(MeldungsartModel.V1, ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT),
          Arguments.of(MeldungsartModel.V3, ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG));
    }
  }
}
