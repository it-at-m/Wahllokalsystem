package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamStatusService {

  private final TeamErfassungStatusValidator teamErfassungStatusValidator;
  private final StimmzettelerfassungTeamStatusRepository stimmzettelerfassungTeamStatusRepository;
  private final TeamErfassungStatusModelMapper teamErfassungStatusModelMapper;
  private final StimmzettelerfassungService stimmzettelerfassungService;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)")
  @Transactional
  public void saveTeamStatus(
      @P("param") final TeamBezirkUndWahlIDModel id,
      final TeamErfassungStatusModel teamErfassungStatusModel) {
    teamErfassungStatusValidator.isValidOrThrow(id);
    teamErfassungStatusValidator.isValidOrThrow(teamErfassungStatusModel);

    val entityToSave = teamErfassungStatusModelMapper.toEntity(id, teamErfassungStatusModel);
    stimmzettelerfassungTeamStatusRepository.save(entityToSave);
    if (TeamErfassungStatusModel.IN_BEARBEITUNG.equals(teamErfassungStatusModel)) {
      stimmzettelerfassungService.registerStimmzettelerfassungStart(
          new BezirkUndWahlID(id.wahlID(), id.wahlbezirkID()));
    }
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)")
  public Optional<TeamErfassungStatusModel> getTeamStatus(
      @P("param") final TeamBezirkUndWahlIDModel id) {
    teamErfassungStatusValidator.isValidOrThrow(id);

    val entityID = teamErfassungStatusModelMapper.toEntity(id);
    val optionalEntityFromRepo = stimmzettelerfassungTeamStatusRepository.findById(entityID);
    return optionalEntityFromRepo.map(
        entity -> teamErfassungStatusModelMapper.toModel(entity.getStatus()));
  }
}
