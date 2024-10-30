package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandService {

    private final ExceptionFactory exceptionFactory;
    private final WahllokalZustandValidator wahllokalZustandValidator;
    private final WahllokalZustandClient wahllokalZustandClient;

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostLastSeen')")
    public void postLastSeen(final String wahlbezirkID) {
        wahllokalZustandValidator.validWahlbezirkIDOrThrow(wahlbezirkID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postLastSeen(wahlbezirkID, LocalDateTime.now());
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostLetzteAbmeldung')")
    public void postLetzteAbmeldung(final String wahlbezirkID) {
        wahllokalZustandValidator.validWahlbezirkIDOrThrow(wahlbezirkID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postLetzteAbmeldung(wahlbezirkID, LocalDateTime.now());
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungSendungsuhrzeit')")
    public void postSchnellmeldungSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungsSendungsuhrzeit) {
        wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postSchnellmeldungSendungsuhrzeit(bezirkUndWahlID, schnellmeldungsSendungsuhrzeit);
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungDruckuhrzeit')")
    public void postSchnellmeldungDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungsDruckuhrzeit) {
        wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postSchnellmeldungDruckuhrzeit(bezirkUndWahlID, schnellmeldungsDruckuhrzeit);
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftSendungsuhrzeit')")
    public void postNiederschriftSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftSendungsuhrzeit) {
        wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postNiederschriftSendungsuhrzeit(bezirkUndWahlID, niederschriftSendungsuhrzeit);
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftDruckuhrzeit')")
    public void postNiederschriftDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftDruckuhrzeit) {
        wahllokalZustandValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG));
        wahllokalZustandClient.postNiederschriftDruckuhrzeit(bezirkUndWahlID, niederschriftDruckuhrzeit);
    }
}
