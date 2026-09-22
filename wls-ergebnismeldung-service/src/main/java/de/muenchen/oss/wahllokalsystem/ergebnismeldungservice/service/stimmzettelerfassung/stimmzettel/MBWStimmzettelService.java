package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.MBWStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MBWStimmzettelService {
  private final StimmzettelValidator stimmzettelValidator;
  private final StimmzettelModelMapper stimmzettelModelMapper;

  private final MBWStimmzettelRepository mbwStimmzettelRepository;

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  // MBW Stapel A
  public Collection<WahlvorschlagStimmzettelAnzahlModel>
      getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
          @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return mbwStimmzettelRepository
        .getStapelA(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
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

    return mbwStimmzettelRepository
        .getStapelB(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
        .stream()
        .map(stimmzettelModelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  public List<KandidatStimmenAnzahlModel> getKandidatVotes(
      @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return mbwStimmzettelRepository
        .getStapelBC(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID())
        .stream()
        .map(stimmzettelModelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('WLS_WAHLVORSTAND')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.getWahlbezirkID(), authentication)")
  public long getCountUngueltige(@P("param") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelValidator.validOrThrow(bezirkUndWahlID);

    return mbwStimmzettelRepository.getStapelD(
        bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID());
  }
}
