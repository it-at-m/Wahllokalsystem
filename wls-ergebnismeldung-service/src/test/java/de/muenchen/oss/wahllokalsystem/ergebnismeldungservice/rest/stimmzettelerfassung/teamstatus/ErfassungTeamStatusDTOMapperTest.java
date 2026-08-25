package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class ErfassungTeamStatusDTOMapperTest {

  ErfassungTeamStatusDTOMapper unitUnderTest =
      Mappers.getMapper(ErfassungTeamStatusDTOMapper.class);

  @Nested
  class ToDTO {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void should_returnDTO_when_givenModel() {
      val modelToMap = Instancio.create(ErfassungTeamStatusModel.class);

      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.name()).isEqualTo(modelToMap.name());
    }

    @ParameterizedTest
    @EnumSource(ErfassungTeamStatusModel.class)
    void should_mapToEnumWithSameName_when_givenModelEnumValue(
        final ErfassungTeamStatusModel modelToMap) {
      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.name()).isEqualTo(modelToMap.name());
    }
  }

  @Nested
  class ToModel {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
    }

    @Test
    void should_returnModel_when_givenDTO() {
      val dtoToMap = Instancio.create(ErfassungTeamStatusDTO.class);

      val result = unitUnderTest.toModel(dtoToMap);

      Assertions.assertThat(result.name()).isEqualTo(dtoToMap.name());
    }

    @ParameterizedTest
    @EnumSource(ErfassungTeamStatusDTO.class)
    void should_mapToEnumWithSameName_when_givenDTOEnumValue(
        final ErfassungTeamStatusDTO dtoToMap) {
      val result = unitUnderTest.toModel(dtoToMap);

      Assertions.assertThat(result.name()).isEqualTo(dtoToMap.name());
    }
  }
}
