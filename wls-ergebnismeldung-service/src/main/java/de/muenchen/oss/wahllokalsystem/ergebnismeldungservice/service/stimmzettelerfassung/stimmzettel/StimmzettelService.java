package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StimmzettelService {

  private final StimmzettelValidator stimmzettelValidator;
  private final StimmzettelModelMapper stimmzettelModelMapper;
  private final StimmzettelRepository stimmzettelRepository;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelOfTeam')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)")
  public List<StimmzettelOfTeamModel> getStimmzettel(
      @P("param") final StimmzettelOwnerModel stimmzettelOwner) {
    stimmzettelValidator.validOrThrow(stimmzettelOwner);

    val entitiesFound =
        stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
            stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());
    return entitiesFound.stream().map(stimmzettelModelMapper::toModel).toList();
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_WriteStimmzettelOfTeam')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
          + " and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#param.teamID(), authentication)")
  @Transactional
  public void saveStimmzettel(
      @P("param") final StimmzettelOwnerModel stimmzettelOwner,
      final List<StimmzettelOfTeamModel> stimmzettelToSave) {
    stimmzettelValidator.validOrThrow(stimmzettelOwner);
    stimmzettelValidator.validOrThrow(stimmzettelToSave);

    stimmzettelRepository.deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
        stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());

    val entitiesToSave =
        stimmzettelToSave.stream()
            .map(stimmzettel -> stimmzettelModelMapper.toEntity(stimmzettelOwner, stimmzettel))
            .toList();
    stimmzettelRepository.saveAll(entitiesToSave);
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_ReadCountStimmzettel')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  public int getAnzahlStimmzettel(@P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return stimmzettelRepository.countByIdWahlbezirkIDAndIdWahlID(
        bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID());
  }
}
