package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.Optional;
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
  private final BedenklicheStimmzettelValidator validator;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetBedenklicheStimmzettelService')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)")
  public Optional<Collection<BedenklicherStimmzettelModel>>
      getBedenklicheStimmzettelOrderedByOrderIndexAsc(
          @P("param") final BezirkUndWahlID bezirkUndWahlID) {
    validator.validateGetBedenklicheStimmzettelParameterOrThrow(bezirkUndWahlID);

    val optionalOfBedenklicheStimmzettel =
        repository.findByBezirkUndWahlIDOrderbyOrderIndexAsc(
            bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID());
    return optionalOfBedenklicheStimmzettel.map(
        bedenklicheStimmzettel ->
            modelMapper.toModel(bedenklicheStimmzettel.getBedenklicheStimmzettel()));
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SetBedenklicheStimmzettelService')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)")
  public void setBedenklicheStimmzettel(
      @P("param") final BezirkUndWahlID bezirkUndWahlID,
      final Collection<BedenklicherStimmzettelModel> bedenklicheStimmzettelToSave) {
    validator.validateSetBedenklicheStimmzettelParameterOrThrow(
        bezirkUndWahlID, bedenklicheStimmzettelToSave);

    val entityToSave =
        modelMapper.toEntity(
            bedenklicheStimmzettelToSave,
            bezirkUndWahlID.getWahlbezirkID(),
            bezirkUndWahlID.getWahlID());
    repository.save(entityToSave);
  }
}
