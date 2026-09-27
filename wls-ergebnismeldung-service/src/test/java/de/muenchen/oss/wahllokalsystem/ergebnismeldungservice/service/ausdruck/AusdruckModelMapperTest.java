package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Dokumentart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndDokumentart;
import java.time.Instant;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class AusdruckModelMapperTest {

  private final AusdruckModelMapper unitUnderTest = Mappers.getMapper(AusdruckModelMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toModel((Ausdruck) null)).isNull();
    }

    @Test
    void should_returnAusdruckReadModel_when_givenAusdruckEntity() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentart = Dokumentart.V1;
      val dokumentartModel = DokumentartModel.V1;
      val content = "Testausdruck";
      val erstelltAm = Instant.now();
      val ausdruckEntity =
          new Ausdruck(
              new WahlUndBezirkIDUndDokumentart(wahlbezirkID, wahlID, dokumentart),
              content,
              erstelltAm);

      val result = unitUnderTest.toModel(ausdruckEntity);

      val expectedResult =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel),
              content,
              erstelltAm);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(Dokumentart.class)
    void should_mapToEnumWithSameName_when_givenEntityDokumentartEnumValue(
        final Dokumentart dokumentart) {
      val entityToMap =
          new Ausdruck(new WahlUndBezirkIDUndDokumentart(null, null, dokumentart), null, null);

      val result = unitUnderTest.toModel(entityToMap);

      Assertions.assertThat(result.wahlUndBezirkIDUndDokumentartModel().dokumentart().name())
          .isEqualTo(DokumentartModel.valueOf(dokumentart.name()).name());
    }
  }

  @Nested
  class ToEntity {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toEntity(null, null)).isNull();
    }

    @Test
    void should_returnAusdruckEntity_when_givenAusdruckWriteModel() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentart = Dokumentart.V1;
      val dokumentartModel = DokumentartModel.V1;
      val content = "Testausdruck";
      val erstelltAm = Instant.now();
      val ausdruckModel =
          new AusdruckWriteModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel),
              content);

      val result = unitUnderTest.toEntity(ausdruckModel, erstelltAm);

      val expectedResult =
          new Ausdruck(
              new WahlUndBezirkIDUndDokumentart(wahlbezirkID, wahlID, dokumentart),
              content,
              erstelltAm);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(DokumentartModel.class)
    void should_mapToEnumWithSameName_when_givenModelDokumentartEnumValue(
        final DokumentartModel dokumentartModel) {
      val modelToMap =
          new AusdruckWriteModel(
              new WahlUndBezirkIDUndDokumentartModel(null, null, dokumentartModel), null);

      val result = unitUnderTest.toEntity(modelToMap, Instant.now());

      Assertions.assertThat(result.getWahlUndBezirkIDUndDokumentart().getDokumentart().name())
          .isEqualTo(dokumentartModel.name());
    }
  }
}
