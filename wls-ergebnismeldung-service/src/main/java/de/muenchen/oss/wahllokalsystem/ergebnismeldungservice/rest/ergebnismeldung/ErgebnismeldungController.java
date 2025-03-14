package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnisseToSendCriteriaModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(description = "Übermitteln einer Ergebnismeldung an das externe System für eine konkrete Wahl eines Wahlbezirkes")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Übermittlung war erfolgreich",
                            content = { @Content() }
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Die Übermittlung konnte auf Grund fehlender Daten nicht durchgeführt werden",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @PostMapping("{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}")
    public ResponseEntity<?> sendErgebnisse(
            @RequestHeader(required = false, name = "forceergebnismeldung") final String forceUpdate,
            @PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer,
            @PathVariable("meldungsart") final MeldungsartDTO meldungsart,
            @PathVariable("hauptwahlbezirkID") final String hauptwahlbezirkID) {
        log.info("sendErgebnisse called with wahlID: {}, wahlbezirkID: {}, waehlerverzeichnisNummer: {}, meldungsart: {}, hauptwahlbezirkID: {}", wahlID,
                wahlbezirkID, waehlerverzeichnisNummer, meldungsart, hauptwahlbezirkID);

        val shouldUpdateSendungszeiten = Boolean.parseBoolean(forceUpdate);
        if (shouldUpdateSendungszeiten) {
            ergebnismeldungService.updateSendungszeiten(new BezirkUndWahlID(wahlID, wahlbezirkID));
        } else {
            ResponseEntity<?> conflictStatus = handleSendErgebnismeldung(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart, hauptwahlbezirkID);
            if (conflictStatus != null) return conflictStatus;
        }

        return ResponseEntity.ok().build();
    }

    @Nullable
    private ResponseEntity<?> handleSendErgebnismeldung(final String wahlID, final String wahlbezirkID, final Long waehlerverzeichnisNummer,
            final MeldungsartDTO meldungsart, final String hauptwahlbezirkID) {
        boolean valid = false;
        try {
            log.debug("#sendergebnis 0");
            val modelEnum = MeldungsartModel.valueOf(meldungsart.name());
            valid = ergebnismeldungService.sendErgebnisse(
                    new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, modelEnum, hauptwahlbezirkID));
            log.debug("#sendergebnis 1");
        } catch (Exception e) {
            log.debug("exception during sendErgebnisse occurred", e);
            if (e instanceof WlsException wlsException)
                return new ResponseEntity<>(dtoMapper.toDTO(wlsException), HttpStatus.CONFLICT);
        }
        if (!valid) {
            log.debug("#sendergebnis 2");
            log.debug("#sendergebnis 3 valid:" + valid);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return null;
    }
}
