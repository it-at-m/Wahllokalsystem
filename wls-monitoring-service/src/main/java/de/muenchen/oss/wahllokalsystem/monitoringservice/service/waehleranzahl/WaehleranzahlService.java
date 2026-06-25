package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.WaehleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaehleranzahlService {

  private final WaehleranzahlValidator waehleranzahlValidator;
  private final WaehleranzahlRepository waehleranzahlRepository;
  private final WaehleranzahlModelMapper waehleranzahlModelMapper;
  private final ExceptionFactory exceptionFactory;
  private final WaehleranzahlClient waehleranzahlClient;

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_GetWahlbeteiligung')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public Optional<WaehleranzahlModel> getWahlbeteiligung(
      @P("bezirkUndWahl") BezirkUndWahlID bezirkUndWahlID) {
    waehleranzahlValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);
    val waehleranzahlFromRepo = waehleranzahlRepository.findById(bezirkUndWahlID);

    return waehleranzahlFromRepo.map(waehleranzahlModelMapper::toModel);
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostWahlbeteiligung')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param.bezirkUndWahlID().wahlbezirkID, authentication)")
  public void postWahlbeteiligung(@P("param") WaehleranzahlModel waehleranzahl) {
    try {
      waehleranzahlRepository.save(waehleranzahlModelMapper.toEntity(waehleranzahl));
    } catch (Exception e) {
      log.error(
          "#postWahlbeteiligung: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden:",
          e);
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE);
    }
    waehleranzahlClient.postWahlbeteiligung(waehleranzahl);
  }
}
