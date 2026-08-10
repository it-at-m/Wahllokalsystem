package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusEntryModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class ErfassungTeamStatusEntryDTOMapperTest {

  ErfassungTeamStatusEntryDTOMapper unitUnderTest =
      Mappers.getMapper(ErfassungTeamStatusEntryDTOMapper.class);

  @Nested
  class ToDTO {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }

    @Test
    void should_mapFields_when_givenModel() {
      val teamId = Instancio.create(String.class);
      val status = Instancio.create(ErfassungTeamStatusModel.class);
      val model = new ErfassungTeamStatusEntryModel(teamId, status);

      val dto = unitUnderTest.toDTO(model);

      val expected =
          new StimmzettelerfassungTeamStatusEntryDTO(
              teamId, ErfassungTeamStatusDTO.valueOf(status.name()));

      Assertions.assertThat(dto).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(ErfassungTeamStatusModel.class)
    void should_mapEnumConsistently_when_givenEachStatus(final ErfassungTeamStatusModel status) {
      val model = new ErfassungTeamStatusEntryModel("team", status);
      val dto = unitUnderTest.toDTO(model);
      Assertions.assertThat(dto.status().name()).isEqualTo(status.name());
    }
  }
}
