package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.WahlUndBezirkIDUndMeldungsartModel;
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
      val meldungsartModel = MeldungsartModel.V1;
      val meldungsartDto = MeldungsartDTO.V1;
      val content = "Testausdruck";
      val erstelltAm = Instant.now();

      val modelToMap =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndMeldungsartModel(wahlbezirkID, wahlID, meldungsartModel),
              content,
              erstelltAm);

      val result = unitUnderTest.toDTO(modelToMap);

      val expectedResult =
          new AusdruckReadDTO(wahlbezirkID, wahlID, meldungsartDto, content, erstelltAm);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(MeldungsartModel.class)
    void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(
        final MeldungsartModel meldungsartModel) {
      val modelToMap =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndMeldungsartModel(null, null, meldungsartModel), null, null);

      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.meldungsart().name()).isEqualTo(meldungsartModel.name());
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
      val meldungsartModel = MeldungsartModel.V1;
      val content = "Testausdruck";

      val dtoToMap = new AusdruckWriteDTO(content);

      val result =
          unitUnderTest.toModel(
              dtoToMap,
              new WahlUndBezirkIDUndMeldungsartModel(wahlbezirkID, wahlID, meldungsartModel));

      val expectedResult =
          new AusdruckWriteModel(
              new WahlUndBezirkIDUndMeldungsartModel(wahlbezirkID, wahlID, meldungsartModel),
              content);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @EnumSource(MeldungsartModel.class)
    void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(
        final MeldungsartModel meldungsartModel) {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val content = "Testausdruck";

      val dtoToMap = new AusdruckWriteDTO(content);

      val result =
          unitUnderTest.toModel(
              dtoToMap,
              new WahlUndBezirkIDUndMeldungsartModel(wahlbezirkID, wahlID, meldungsartModel));

      Assertions.assertThat(result.wahlUndBezirkIDUndMeldungsartModel().meldungsart().name())
          .isEqualTo(meldungsartModel.name());
    }
  }
}
