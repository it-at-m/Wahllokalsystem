package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MBWBedenklicheStimmzettelService {

  private final BedenklicheStimmzettelRepository repository;
  private final BedenklicheStimmzettelModelMapper modelMapper;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetBedenklicheStimmzettelService')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)")
  public Collection<BedenklicheStimmzettelModel> getBedenklicheStimmzettelOrderedByOrderIndexAsc(
      @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    return repository
        .findByBezirkUndWahlIDOrderbyOrderIndexAsc(
            bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID())
        .stream()
        .map(modelMapper::toModel)
        .toList();
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SetBedenklicheStimmzettelService')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)")
  public void setBedenklicheStimmzettel(
      @P("param") final BezirkUndWahlID bezirkUndWahlID,
      Collection<BedenklicheStimmzettelModel> bedenklicheStimmzettelToSave) {
    val entitiesToSave =
        bedenklicheStimmzettelToSave.stream()
            .map(
                stimmzettel ->
                    modelMapper.toEntity(
                        stimmzettel,
                        bezirkUndWahlID.getWahlbezirkID(),
                        bezirkUndWahlID.getWahlID()))
            .toList();
    repository.saveAll(entitiesToSave);
  }
}
