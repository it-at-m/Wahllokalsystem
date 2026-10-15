package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.DokumentartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.DokumentartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.WahlUndBezirkIDUndDokumentartModel;
import java.time.Instant;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class AusdruckDTOMapperTest {

  AusdruckDTOMapper unitUnderTest = Mappers.getMapper(AusdruckDTOMapper.class);

  @Nested
  class ToDTO {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void should_returnAusdruckReadDTO_when_givenAusdruckReadModel() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentartModel = DokumentartModel.V1;
      val dokumentartDto = DokumentartDTO.V1;
      val content = "Testausdruck";
      val erstelltAm = Instant.now();

      val modelToMap =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel),
              content,
              erstelltAm);

      val result = unitUnderTest.toDTO(modelToMap);

      val expectedResult =
          new AusdruckReadDTO(wahlbezirkID, wahlID, dokumentartDto, content, erstelltAm);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(DokumentartModel.class)
    void should_mapToEnumWithSameMeaning_when_givenModelDokumentartEnumValue(
        final DokumentartModel dokumentartModel) {
      val modelToMap =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndDokumentartModel(null, null, dokumentartModel), null, null);

      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.dokumentart().name()).isEqualTo(dokumentartModel.name());
    }
  }

  @Nested
  class ToModel {
    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toModel(null, null)).isNull();
    }

    @Test
    void should_returnAusdruckWriteModel_when_givenAusdruckWriteDTO() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentartModel = DokumentartModel.V1;
      val content = "Testausdruck";

      val dtoToMap = new AusdruckWriteDTO(content);

      val result =
          unitUnderTest.toModel(
              dtoToMap,
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel));

      val expectedResult =
          new AusdruckWriteModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel),
              content);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(DokumentartModel.class)
    void should_mapToEnumWithSameName_when_givenModelDokumentartEnumValue(
        final DokumentartModel dokumentartModel) {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val content = "Testausdruck";

      val dtoToMap = new AusdruckWriteDTO(content);

      val result =
          unitUnderTest.toModel(
              dtoToMap,
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel));

      Assertions.assertThat(result.wahlUndBezirkIDUndDokumentartModel().dokumentart().name())
          .isEqualTo(dokumentartModel.name());
    }
  }
}
