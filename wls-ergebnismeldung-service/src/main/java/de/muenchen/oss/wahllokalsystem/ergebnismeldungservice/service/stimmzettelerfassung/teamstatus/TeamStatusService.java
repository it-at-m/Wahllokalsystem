package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamStatusService {

  private final TeamErfassungStatusValidator teamErfassungStatusValidator;
  private final StimmzettelerfassungTeamStatusRepository stimmzettelerfassungTeamStatusRepository;
  private final TeamErfassungStatusModelMapper teamErfassungStatusModelMapper;

  public void saveTeamStatus(
      final WahlbezirkErfassungsteamID id,
      final TeamErfassungStatusModel teamErfassungStatusModel) {
    teamErfassungStatusValidator.isValidOrThrow(id);
    teamErfassungStatusValidator.isValidOrThrow(teamErfassungStatusModel);

    val entityToSave = teamErfassungStatusModelMapper.toEntity(id, teamErfassungStatusModel);
    stimmzettelerfassungTeamStatusRepository.save(entityToSave);
  }

  public Optional<TeamErfassungStatusModel> getTeamStatus(final WahlbezirkErfassungsteamID id) {
    teamErfassungStatusValidator.isValidOrThrow(id);

    val entityID = teamErfassungStatusModelMapper.toEntity(id);
    val optionalEntityFromRepo = stimmzettelerfassungTeamStatusRepository.findById(entityID);
    return optionalEntityFromRepo.map(
        entity -> teamErfassungStatusModelMapper.toModel(entity.getStatus()));
  }
}
