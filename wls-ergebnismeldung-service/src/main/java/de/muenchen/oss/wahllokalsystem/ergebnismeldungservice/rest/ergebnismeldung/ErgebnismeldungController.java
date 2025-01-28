package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnisseToSendCriteriaModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/sendErgebnismeldung")
@RequiredArgsConstructor
@Slf4j
public class ErgebnismeldungController {

    private final ErgebnismeldungService ergebnismeldungService;
    private final DTOMapper dtoMapper;

    @PostMapping("{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}")
    public ResponseEntity<?> sendErgebnisse(
            @RequestHeader(required = false, name = "forceergebnismeldung") final String forceUpdate,
            SendErgebnisParameter sendErgebnisParameter) {
        log.info("sendErgebnisse called with {}", sendErgebnisParameter);

        val shouldUpdateSendungszeiten = Boolean.parseBoolean(forceUpdate);
        if (shouldUpdateSendungszeiten) {
            ergebnismeldungService.updateSendungszeiten(new BezirkUndWahlID(sendErgebnisParameter.wahlbezirkID(), sendErgebnisParameter.wahlID()));
        } else {
            boolean valid = false;
            try {
                log.debug("#sendergebnis 0");
                val modelEnum = MeldungsartModel.valueOf(sendErgebnisParameter.meldungsart().name());
                valid = ergebnismeldungService.sendErgebnisse(
                        new ErgebnisseToSendCriteriaModel(sendErgebnisParameter.wahlID(), sendErgebnisParameter.wahlbezirkID(),
                                sendErgebnisParameter.waehlerverzeichnisNummer(), modelEnum,
                                sendErgebnisParameter.hauptwahlbezirkID()));
                log.debug("#sendergebnis 1");
            } catch (Exception e) {
                if (e != null && e instanceof WlsException wlsException)
                    return new ResponseEntity<>(dtoMapper.toDTO(wlsException), HttpStatus.CONFLICT);
            }
            if (!valid) {
                log.debug("#sendergebnis 2");
                log.debug("#sendergebnis 3 valid:" + valid);
                // this shoud not be reached, because expected is a WlsException if valid =
                // false, it is only fot just in case..
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        return ResponseEntity.ok().build();
    }
}


