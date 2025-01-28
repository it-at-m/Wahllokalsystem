package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultElectionTypeValidator {

    private final ExceptionFactory exceptionFactory;

    private final AWerteRepository aWerteRepo;
    private final ErgebnisseRepository ergebnisseRepo;
    private final StimmabgabevermerkeRepository stimmabgabevermerkeRepo;
    private final WahlscheineRepository wahlscheineRepo;
    private final AWerteService aWerte_BusinessActionService;

    /**
     * Validiert gemaess wahlart und wahlbezirkart. wahlbezirkID und wahlID werden für das Laden der benötigten Daten aus den repos benötigt. Wird von den
     * checkValidation(...) der abgeleiteten Klassen gerufen, diese erzeugen auch benoetigteStapel.
     */
    public boolean checkValidation(WahlbezirkArtModel wahlbezirkart,
            String wahlbezirkID,
            String wahlID,
            Long waehlerverzeichnisNummer,
            List<Stapelart> benoetigteStapel) throws WlsException {

        log.debug("#sendergebnis defaultvalidator checkvalidation 0");
        boolean valid = checkValidationStapel(wahlbezirkID, wahlID, benoetigteStapel);
        log.debug("#sendergebnis defaultvalidator checkvalidation stapel valid {}", valid);

        if (wahlbezirkart == WahlbezirkArtModel.BWB) {
            valid = valid && checkValidationWahlscheine(wahlbezirkID, wahlID);
            log.debug("#sendergebnis defaultvalidator bwb checkvalidation wahlscheine valid {}", valid);
        } else {
            valid = valid && checkValidationStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer);
            log.debug("#sendergebnis defaultvalidator checkvalidation sav valid {}", valid);
            valid = valid && checkAWerte(wahlbezirkID, wahlID);
            log.debug("#sendergebnis defaultvalidator checkvalidation awerte valid {}", valid);
        }

        return valid;
    }

    private boolean checkValidationStapel(String wahlbezirkID, String wahlID, List<Stapelart> benoetigteStapel) throws WlsException {

        // Ermittle vorhandene Stapelarten aus ergebnisRepo.ergebnisseList.ergebnisse.bezirksUndWahlIdStapelart und
        // lege sie in einem HashSet ab.
        val stapelartList = new HashSet<>();
        val ergebnisseList = ergebnisseRepo.findByWahlbezirkIDAndWahlD(wahlbezirkID, wahlID);
        for (Ergebnisse ergebnisse : ergebnisseList) {
            stapelartList.add(ergebnisse.getBezirkUndWahlIDStapelart().getStapelart());
        }

        for (Stapelart stapelart : benoetigteStapel) {
            if (!stapelartList.contains(stapelart)) {
                log.error("Wahlbezirk {} hat Stapel {} noch nicht erfasst. Invalide!", wahlbezirkID, stapelart);
                val exceptionWrapperForStapel = new ExceptionDataWrapper(ExceptionConstants.SENDERGEBNISSE_STAPELN_UNVOLLSTAENDIG.code(),
                        ExceptionConstants.SENDERGEBNISSE_STAPELN_UNVOLLSTAENDIG.message() + " " + stapelart);
                throw exceptionFactory.createFachlicheWlsException(exceptionWrapperForStapel);
            }
        }
        return true;
    }

    private boolean checkValidationStimmabgabevermerke(String wahlbezirkID,
            Long waehlerverzeichnisNummer) throws WlsException {
        val bezirkIDUndWaehlerverzeichnisNummer = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID,
                waehlerverzeichnisNummer);

        Optional<Stimmabgabevermerke> stimmabgabevermerke = stimmabgabevermerkeRepo.findById(bezirkIDUndWaehlerverzeichnisNummer);

        // Nur dann Fehlermeldung erzeugen, wenn stimmabgabevermerke null ist.
        boolean stimmabgabevermerkeErfasst = stimmabgabevermerke.isPresent();
        if (!stimmabgabevermerkeErfasst) {
            log.error("Urnenwahlbezirk {} hat Stimmabgabevermerke noch nicht erfasst. Invalide!", wahlbezirkID);
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_STIMMABGABEVERMERKE_UNVOLLSTAENDIG);
        }
        return stimmabgabevermerkeErfasst;
    }

    private boolean checkValidationWahlscheine(String wahlbezirkID, String wahlID) throws WlsException {
        val wahlscheine = wahlscheineRepo.findById(new BezirkUndWahlID(wahlbezirkID, wahlID));
        boolean wahlscheineErfasst = wahlscheine.isPresent();
        if (!wahlscheineErfasst) {
            log.error("Briefwahlbezirk {} hat Wahlscheine noch nicht erfasst. Invalide!", wahlbezirkID);
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_WAHLSCHEINE_UNVOLLSTAENDIG);
        }
        return wahlscheineErfasst;
    }

    private boolean checkAWerte(String wahlbezirkID, String wahlID) throws WlsException {
        val bezirkUndWahlID = new BezirkUndWahlID(wahlbezirkID, wahlID);
        Optional<AWerte> aWerte = aWerteRepo.findById(new BezirkUndWahlID(wahlbezirkID, wahlID));
        if (aWerte.isEmpty()) {
            aWerte_BusinessActionService.getAWerte(wahlbezirkID);
            aWerte = aWerteRepo.findById(bezirkUndWahlID);
            boolean aWerteVorhanden = aWerte.isPresent();
            if (!aWerteVorhanden) {
                log.error("Keine A-Werte für Urnenwahlbezirk {} gefunden. Invalide!", wahlbezirkID);
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_AWERTE_UNVOLLSTAENDIG);
            }
            return aWerteVorhanden;
        }
        return true;
    }
}
