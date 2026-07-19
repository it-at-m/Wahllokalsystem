package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import static org.assertj.core.api.Assertions.assertThat;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamErfassungStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TeamErfassungStatusModelMapperTest {

  private final TeamErfassungStatusModelMapper mapper =
      Mappers.getMapper(TeamErfassungStatusModelMapper.class);

  @Test
  void should_map_entity_to_model_and_back_for_all_values() {
    for (TeamErfassungStatus entityValue : TeamErfassungStatus.values()) {
      TeamErfassungStatusModel model = mapper.toModel(entityValue);
      StimmzettelerfassungTeamStatus mappedBack =
          mapper.toEntity(Instancio.create(WahlbezirkErfassungsteamID.class), model);

      assertThat(model).isNotNull();
      assertThat(mappedBack.getStatus()).isEqualTo(entityValue);
    }
  }
}
