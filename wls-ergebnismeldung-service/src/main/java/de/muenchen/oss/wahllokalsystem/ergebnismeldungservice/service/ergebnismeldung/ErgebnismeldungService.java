package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErgebnismeldungService {

    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private final ErgebnismeldungValidator ergebnismeldungValidator;
    private final ExceptionFactory exceptionFactory;
    private final Collection<AuthenticationHandler> authenticationHandlers;

    private final UrnenwahlClient urnenwahlClient;
    private final StimmzettelumschlaegeService stimmzettelumschlaegeService;
    private final StatusService statusService;
    private final StatusClient statusClient;

    @PreAuthorize(
            "hasAuthority('Ergebnismeldung_BUSINESSACTION_ForceErgebnisse')"
                    + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID(), authentication)"
    )
    public void updateSendungszeiten(@P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID) {
        log.info("#updateSendungszeiten");

        ergebnismeldungValidator.validBezirkUndWahlIDOrThrow(bezirkUndWahlID);
        assertWahlIsGeschlossen(bezirkUndWahlID);

        val status = statusService.getStatus(bezirkUndWahlID);
        status.ifPresent(this::sendRelevantStatus);
    }

    private void assertWahlIsGeschlossen(final BezirkUndWahlID bezirkUndWahlID) {
        WahlbezirkArtModel wahlbezirkart = getWahlbezirkArtOfCurrentAuthentication();
        boolean isGeschlossen;
        try {
            isGeschlossen = switch (wahlbezirkart) {
                case UWB -> urnenwahlClient.isGeschlossen(bezirkUndWahlID.getWahlbezirkID());
                case BWB -> {
                    val umschlaegeOfWahlbezirk = stimmzettelumschlaegeService.getStimmzettelumschlaege(bezirkUndWahlID);
                    yield umschlaegeOfWahlbezirk.filter(stimmzettelumschlaegeModel -> stimmzettelumschlaegeModel.urneneroeffnungsUhrzeit() != null)
                            .isPresent();
                }
            };
        } catch (final Exception e) {
            log.error("#isWahlGeschlossen exception beim Versuch die Schließungsuhrzeit zu laden: {}", e.getMessage(), e);
            isGeschlossen = true; //TODO ist das schlau anzunehmen dass bei einem Kommunikationsfehler das Lokal geschlossen ist?
        }

        if (!isGeschlossen) {
            log.error("Es wurde keine Schließungsuhrzeit für die Wahl {} vom Bezirk {} erfasst", bezirkUndWahlID.getWahlID(),
                    bezirkUndWahlID.getWahlbezirkID());
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE);
        }
    }

    private WahlbezirkArtModel getWahlbezirkArtOfCurrentAuthentication() {
        /* TODO mit der Logik aus dem Stimmzettelumschlägen zusammenlegen */
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        try {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail(WAHLBEZIRK_ART_USER_DETAIL_KEY, currentAuthentication);
            return wahlbezirkOfUser.map(WahlbezirkArtModel::valueOf).get();
        } catch (Exception e) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE);
        }
    }

    private void sendRelevantStatus(final StatusModel status) {
        val validierungsStatusSchnellmeldung = status.schnellmeldung().validierungsstatus();
        val validierungsStatusNiederschrift = status.niederschrift().validierungsstatus();
        val now = LocalDateTime.now();

        if (validierungsStatusNiederschrift.equals(ValidierungsstatusModel.NICHT_GESENDET)) {
            if (!validierungsStatusSchnellmeldung.equals(ValidierungsstatusModel.NICHT_GESENDET)) {
                //status.getSchnellmeldung().setValidierungsstatus(Validierungsstatus_.INVALIDE);
                try {
                    statusClient.postSchnellmeldungSendungsuhrzeit(status.bezirkUndWahlID(), now);
                } catch (Exception e) {
                    log.error("#postSchnellmeldungsSendungsuhrzeit Exception:", e);
                }
            } else {
                log.error("#forceErgebnisse Error: forceErgebnisse sollte nie in Benutzung sein während beide Validierungstatus aus NICHT_GESENDET stehen.");
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.FORCEERGEBNISSE_WRONG_USAGE);
            }
        } else {
            try {
                statusClient.postNiederschriftSendungsuhrzeit(status.bezirkUndWahlID(), now);
            } catch (Exception e) {
                log.error("#postNiederschriftSendungsuhrzeit Exception:", e);
            }
        }
    }
}
