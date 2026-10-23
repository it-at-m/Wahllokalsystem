package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.ErfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamStatusService {

  private final ErfassungTeamStatusValidator erfassungTeamStatusValidator;
  private final StimmzettelerfassungTeamStatusRepository stimmzettelerfassungTeamStatusRepository;
  private final ErfassungTeamStatusModelMapper erfassungTeamStatusModelMapper;
  private final StimmzettelerfassungService stimmzettelerfassungService;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and ("
          + "@teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)"
          + " or hasAuthority('WLS_WAHLVORSTAND')"
          + ")")
  @Transactional
  public ErfassungTeamStatusModel saveTeamStatus(
      @P("param") final TeamBezirkUndWahlIDModel id,
      final ErfassungTeamStatusModel erfassungTeamStatusModel) {
    erfassungTeamStatusValidator.isValidOrThrow(id);
    erfassungTeamStatusValidator.isValidOrThrow(erfassungTeamStatusModel);

    val isStimmzettelErfassungAbgeschlossen =
        stimmzettelerfassungService.isStimmzettelerfassungAbgeschlossen(
            erfassungTeamStatusModelMapper.toBezirkUndWahlID(id));

    final StimmzettelerfassungTeamStatus entityToSave;
    if (isStimmzettelErfassungAbgeschlossen) {
      entityToSave =
          erfassungTeamStatusModelMapper.toEntity(id, ErfassungTeamStatusModel.ABGESCHLOSSEN);
    } else {
      entityToSave = erfassungTeamStatusModelMapper.toEntity(id, erfassungTeamStatusModel);
    }

    stimmzettelerfassungTeamStatusRepository.save(entityToSave);
    if (ErfassungTeamStatus.IN_BEARBEITUNG.equals(entityToSave.getStatus())) {
      stimmzettelerfassungService.registerStimmzettelerfassungStart(
          new BezirkUndWahlID(id.wahlID(), id.wahlbezirkID()));
    }

    return erfassungTeamStatusModelMapper.toModel(entityToSave.getStatus());
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)")
  public Optional<ErfassungTeamStatusModel> getTeamStatus(
      @P("param") final TeamBezirkUndWahlIDModel id) {
    erfassungTeamStatusValidator.isValidOrThrow(id);

    val entityID = erfassungTeamStatusModelMapper.toEntity(id);
    val optionalEntityFromRepo = stimmzettelerfassungTeamStatusRepository.findById(entityID);
    return optionalEntityFromRepo.map(
        entity -> erfassungTeamStatusModelMapper.toModel(entity.getStatus()));
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  public List<ErfassungTeamStatusEntryModel> getTeamStatusList(
      @P("param") final BezirkUndWahlID id) {
    val entities =
        stimmzettelerfassungTeamStatusRepository.findByIdWahlIDAndIdWahlbezirkID(
            id.getWahlID(), id.getWahlbezirkID());
    return entities.stream()
        .map(erfassungTeamStatusModelMapper::toEntryModel)
        .collect(Collectors.toList());
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungTeamstatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
          + " and ("
          + "@teamIDPermissionEvaluator.tokenUserteamIdMatches(#teamID, authentication)"
          + " or hasAuthority('WLS_WAHLVORSTAND')"
          + ")")
  @Transactional
  public void reopenStimmzettelerfassung(
      final String wahlID,
      @P("wahlbezirkID") final String wahlbezirkID,
      @P("teamID") final String teamID) {
    stimmzettelerfassungService.registerStimmzettelerfassungStart(
        new BezirkUndWahlID(wahlID, wahlbezirkID));
    saveTeamStatus(
        new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID),
        ErfassungTeamStatusModel.IN_BEARBEITUNG);
  }
}
