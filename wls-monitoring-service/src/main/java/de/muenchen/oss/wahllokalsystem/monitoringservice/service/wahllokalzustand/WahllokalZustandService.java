package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandService {

  private final ExceptionFactory exceptionFactory;
  private final WahllokalZustandValidator wahllokalZustandValidator;
  private final WahllokalZustandClient wahllokalZustandClient;

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostLastSeen')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
          + "and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#teamID, authentication)")
  public void postLastSeen(
      @P("wahlbezirkID") final String wahlbezirkID, @P("teamID") final String teamID) {
    wahllokalZustandValidator.validWahlbezirkIDOrThrow(
        wahlbezirkID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandValidator.validTeamIDOrThrow(
        teamID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postLastSeen(wahlbezirkID, teamID, LocalDateTime.now());
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostLetzteAbmeldung')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
          + "and @teamIDPermissionEvaluator.tokenUserteamIdMatches(#teamID, authentication)")
  public void postLetzteAbmeldung(
      @P("wahlbezirkID") final String wahlbezirkID, @P("teamID") final String teamID) {
    wahllokalZustandValidator.validWahlbezirkIDOrThrow(
        wahlbezirkID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandValidator.validTeamIDOrThrow(
        teamID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postLetzteAbmeldung(wahlbezirkID, teamID, LocalDateTime.now());
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungSendungsuhrzeit')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public void postSchnellmeldungSendungsuhrzeit(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID,
      final LocalDateTime schnellmeldungSendungsuhrzeit) {
    wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postSchnellmeldungSendungsuhrzeit(
        bezirkUndWahlID, schnellmeldungSendungsuhrzeit);
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungDruckuhrzeit')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public void postSchnellmeldungDruckuhrzeit(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID,
      final LocalDateTime schnellmeldungsDruckuhrzeit) {
    wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postSchnellmeldungDruckuhrzeit(
        bezirkUndWahlID, schnellmeldungsDruckuhrzeit);
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftSendungsuhrzeit')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public void postNiederschriftSendungsuhrzeit(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID,
      final LocalDateTime niederschriftSendungsuhrzeit) {
    wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postNiederschriftSendungsuhrzeit(
        bezirkUndWahlID, niederschriftSendungsuhrzeit);
  }

  @PreAuthorize(
      "hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftDruckuhrzeit')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public void postNiederschriftDruckuhrzeit(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID,
      final LocalDateTime niederschriftDruckuhrzeit) {
    wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
    wahllokalZustandClient.postNiederschriftDruckuhrzeit(
        bezirkUndWahlID, niederschriftDruckuhrzeit);
  }
}
