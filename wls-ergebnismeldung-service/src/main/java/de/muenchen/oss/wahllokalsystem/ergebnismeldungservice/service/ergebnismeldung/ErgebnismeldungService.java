package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.Mapping;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ErgebnismeldungValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErgebnismeldungService {

    private static final Logger SYSLOGGER = LoggerFactory.getLogger("ERGEBNISMELDUNG_SERVICE_SIEM_LOGGER");

    private final ErgebnismeldungValidator ergebnismeldungValidator;
    private final ExceptionFactory exceptionFactory;
    private final ErgebnismeldungMappingService ergebnismeldungMappingService;
    private final Mapping mapping;

    private final UrnenwahlClient urnenwahlClient;
    private final WahlenClient wahlenClient;
    private final EaiService eaiService;
    private final StimmzettelumschlaegeService stimmzettelumschlaegeService;
    private final StatusService statusService;
    private final StatusClient statusClient;
    private final AuthenticationService authenticationService;

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_ForceErgebnisse')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)"
    )
    public void updateSendungszeiten(@P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID) {
        log.info("#updateSendungszeiten");

        ergebnismeldungValidator.validBezirkUndWahlIDOrThrow(bezirkUndWahlID);
        assertWahlIsGeschlossen(bezirkUndWahlID);

        val status = statusService.getStatus(bezirkUndWahlID);
        status.ifPresent(this::sendRelevantStatus);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_SendErgebnisse')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#criteria.wahlbezirkID, authentication)"
    )
    public boolean sendErgebnisse(@P("criteria") final ErgebnisseToSendCriteriaModel ergebnisseToSendCriteria) {
        ergebnismeldungValidator.validErgebnisseToSendCriteriaOrThrow(ergebnisseToSendCriteria);
        log.info("#sendErgebnisse hauptwahlbezirkID {}", ergebnisseToSendCriteria.hauptwahlbezirkID());

        assertWahlIsGeschlossen(new BezirkUndWahlID(ergebnisseToSendCriteria.wahlID(), ergebnisseToSendCriteria.wahlbezirkID()));

        val wahlart = wahlenClient.getWahlartOfCurrentWahltag(ergebnisseToSendCriteria.wahlID());
        val wahlbezirkArt = authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow();

        log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 1");
        val valid = ergebnismeldungValidator.checkValidation(wahlart, wahlbezirkArt, ergebnisseToSendCriteria.wahlbezirkID(),
                ergebnisseToSendCriteria.wahlID(), ergebnisseToSendCriteria.waehlerverzeichnisNummer(), ergebnisseToSendCriteria.meldungsart());
        log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 2");

        val eaiMeldungsart = mapping.toDTO(ergebnisseToSendCriteria.meldungsart());

        if (valid) {
            log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3 valid: {}", valid);
            sendErgebnisseToEAI(
                    ergebnismeldungMappingService.createErgebnismeldung(wahlart, ergebnisseToSendCriteria.wahlID(), ergebnisseToSendCriteria.wahlbezirkID(),
                            ergebnisseToSendCriteria.waehlerverzeichnisNummer(), eaiMeldungsart,
                            ergebnisseToSendCriteria.hauptwahlbezirkID()));
            log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 4 valid: {}", valid);
        }
        return valid;
    }

    private void sendErgebnisseToEAI(final ErgebnismeldungDTO ergebnismeldung) {
        log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.1 a sendErgebnisseToEAI" + ergebnismeldung);
        eaiService.sendErgebnismeldung(ergebnismeldung);
        log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.1 b sendErgebnisseToEAI");
        sendSendungsuhrzeiten(ergebnismeldung);
        log.debug("SENDERGEBNISSE BUSINESSAKTION #sendergebnis 3.1 c sendErgebnisseToEAI");
    }

    private void assertWahlIsGeschlossen(final BezirkUndWahlID bezirkUndWahlID) {
        WahlbezirkArtModel wahlbezirkart = authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow();
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
            isGeschlossen = true;
        }

        if (!isGeschlossen) {
            log.error("Es wurde keine Schließungsuhrzeit für die Wahl {} vom Bezirk {} erfasst", bezirkUndWahlID.getWahlID(),
                    bezirkUndWahlID.getWahlbezirkID());
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHL_NICHT_GESCHLOSSEN);
        }
    }

    private void sendRelevantStatus(final StatusModel status) {
        val validierungsStatusSchnellmeldung = status.schnellmeldung().validierungsstatus();
        val validierungsStatusNiederschrift = status.niederschrift().validierungsstatus();
        val now = LocalDateTime.now();

        if (validierungsStatusNiederschrift.equals(ValidierungsstatusModel.NICHT_GESENDET)) {
            if (!validierungsStatusSchnellmeldung.equals(ValidierungsstatusModel.NICHT_GESENDET)) {
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

    private void sendSendungsuhrzeiten(final ErgebnismeldungDTO ergebnismeldung) {
        val now = LocalDateTime.now();
        try {
            val meldungsart = ergebnismeldung.getMeldungsart(); // meldungsart kann null sein, dann fliegt eine Exception!
            logErgebnismeldungGesendet(meldungsart.name(), ergebnismeldung.getWahlID());

            if (meldungsart.equals(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT)) {
                statusClient.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(ergebnismeldung.getWahlID(), ergebnismeldung.getWahlbezirkID()), now);
            } else if (meldungsart.equals(ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG)) {
                statusClient.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(ergebnismeldung.getWahlID(), ergebnismeldung.getWahlbezirkID()), now);
            }
        } catch (final Exception ex) {
            // Do not throw exception bug log it in Logfile. Monitoring cannot cripple main functionality
            log.error("sendSendungsstatus Fehler: {}", ex);
        }
    }

    private void logErgebnismeldungGesendet(final String meldungsart, final String wahlID) {
        val now = LocalDateTime.now();
        val eid = meldungsart.equals(MeldungsartModel.V3.name()) ? "SCHNELLMELDUNG_GESENDET" : "NIEDERSCHRIFT_GESENDET";

        try {
            MDC.put("eid", eid);
            MDC.put("result", "0");
            SYSLOGGER.info("wahlId=" + wahlID + "|sendingTime=" + now.toString() + "|");
        } finally {
            MDC.remove("eid");
            MDC.remove("result");
        }
    }
}
