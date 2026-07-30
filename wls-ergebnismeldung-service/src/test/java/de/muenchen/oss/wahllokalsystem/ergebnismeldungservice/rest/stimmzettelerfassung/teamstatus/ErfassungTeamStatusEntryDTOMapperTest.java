package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusEntryModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
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
    void should_map_fields_when_given_model() {
      final String teamId = Instancio.create(String.class);
      final ErfassungTeamStatusModel status = Instancio.create(ErfassungTeamStatusModel.class);
      final var model = new ErfassungTeamStatusEntryModel(teamId, status);

      final var dto = unitUnderTest.toDTO(model);

      Assertions.assertThat(dto.teamID()).isEqualTo(teamId);
      Assertions.assertThat(dto.status().name()).isEqualTo(status.name());
    }

    @ParameterizedTest
    @EnumSource(ErfassungTeamStatusModel.class)
    void should_map_enum_consistently_when_given_each_status(
        final ErfassungTeamStatusModel status) {
      final var model = new ErfassungTeamStatusEntryModel("team", status);
      final var dto = unitUnderTest.toDTO(model);
      Assertions.assertThat(dto.status().name()).isEqualTo(status.name());
    }
  }
}
