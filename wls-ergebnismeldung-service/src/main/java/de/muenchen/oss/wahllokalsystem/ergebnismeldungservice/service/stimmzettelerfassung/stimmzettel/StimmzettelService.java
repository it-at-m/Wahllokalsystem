package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.logging.PerformanceLogging;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelJdbcRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StimmzettelService {

  private static final Logger getStimmzettelPerformanceLogger = PerformanceLogging.createPerformanceLogger(StimmzettelService.class.getName() + ".getStimmzettel");

  private final StimmzettelValidator stimmzettelValidator;
  private final StimmzettelModelMapper stimmzettelModelMapper;
  private final StimmzettelRepository stimmzettelRepository;
  private final StimmzettelJdbcRepository stimmzettelJdbcRepository;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelOfTeam')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and ("
          + "@teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)"
          + " or hasAuthority('WLS_WAHLVORSTAND')"
          + ")")
  public List<StimmzettelOfTeamModel> getStimmzettel(
      @P("param") final TeamBezirkUndWahlIDModel stimmzettelOwner) {
    val totalStopWatch = StopWatch.createStarted();
    stimmzettelValidator.validOrThrow(stimmzettelOwner);

    val repoFindStopWatch = StopWatch.createStarted();
    val entitiesFound = stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());
    stimmzettelRepository.readKandidaten(entitiesFound.stream().flatMap(s -> s.getWahlvorschlaege().stream()).toList());
    stimmzettelRepository.readWahlvorstandBeschlussvorschlag(entitiesFound);
    stimmzettelRepository.readSystemBeschlussvorschlag(entitiesFound);
    repoFindStopWatch.stop();
    getStimmzettelPerformanceLogger.atInfo().log("repoFind {} ms", repoFindStopWatch.getDuration().toMillis());

    val toModelStopWatch = StopWatch.createStarted();
    val stimmzettelModels = entitiesFound.stream().map(stimmzettelModelMapper::toModel).toList();
    toModelStopWatch.stop();
    getStimmzettelPerformanceLogger.atInfo().log("toModel {} ms", toModelStopWatch.getDuration().toMillis());

    totalStopWatch.stop();

    getStimmzettelPerformanceLogger.atInfo().log("total {} ms", totalStopWatch.getDuration().toMillis());
    return stimmzettelModels;
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_WriteStimmzettelOfTeam')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and ("
          + "@teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)"
          + " or hasAuthority('WLS_WAHLVORSTAND')"
          + ")")
  @Transactional
  public void saveStimmzettel(
      @P("param") final TeamBezirkUndWahlIDModel stimmzettelOwner,
      final List<StimmzettelOfTeamModel> stimmzettelToSave) {
    stimmzettelValidator.validOrThrow(stimmzettelOwner);
    stimmzettelValidator.validOrThrow(stimmzettelToSave);

//    stimmzettelRepository.deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
//        stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());

    val entitiesToSave =
        stimmzettelToSave.stream()
            .map(stimmzettel -> stimmzettelModelMapper.toEntity(stimmzettelOwner, stimmzettel))
            .toList();
//    stimmzettelRepository.saveAll(entitiesToSave);
    stimmzettelJdbcRepository.replaceStimmzettel(stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID(), entitiesToSave);
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_ReadCountStimmzettel')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  public int getAnzahlStimmzettel(@P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository.countByIdWahlbezirkIDAndIdWahlID(
        bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID());
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  // MBW Stapel A
  public Collection<WahlvorschlagStimmzettelAnzahlModel>
      getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
          @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository
        .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
            bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
        .stream()
        .map(stimmzettelModelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  // MBW Stapel B
  public Collection<WahlvorschlagStimmzettelAnzahlModel>
      countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
          @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository
        .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
            bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
        .stream()
        .map(stimmzettelModelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  // MBW Stapel BC
  public List<KandidatStimmenAnzahlModel> getKandidatVotes(
      @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository
        .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
            bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
        .stream()
        .map(stimmzettelModelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  // MBW Stapel D Ungueltig
  public long getCountUngueltige(@P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository.countInvalidStimmzettel(
        bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID());
  }
}
