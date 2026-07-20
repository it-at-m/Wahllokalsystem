package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.TeamErfassungStatusModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class TeamErfassungStatusDTOMapperTest {

  TeamErfassungStatusDTOMapper unitUnderTest =
      Mappers.getMapper(TeamErfassungStatusDTOMapper.class);

  @Nested
  class ToDTO {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void should_returnDTO_when_givenModel() {
      val modelToMap = Instancio.create(TeamErfassungStatusModel.class);

      val result = unitUnderTest.toDTO(modelToMap);

      Assertions.assertThat(result.name()).isEqualTo(modelToMap.name());
    }

    @ParameterizedTest
    @EnumSource(TeamErfassungStatusModel.class)
    void should_mapToEnumWithSameName_when_givenModelEnumValue(
        final TeamErfassungStatusModel modelToMap) {
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
      val dtoToMap = Instancio.create(TeamErfassungStatusDTO.class);

      val result = unitUnderTest.toModel(dtoToMap);

      Assertions.assertThat(result.name()).isEqualTo(dtoToMap.name());
    }

    @ParameterizedTest
    @EnumSource(TeamErfassungStatusDTO.class)
    void should_mapToEnumWithSameName_when_givenDTOEnumValue(
        final TeamErfassungStatusDTO dtoToMap) {
      val result = unitUnderTest.toModel(dtoToMap);

      Assertions.assertThat(result.name()).isEqualTo(dtoToMap.name());
    }
  }
}
