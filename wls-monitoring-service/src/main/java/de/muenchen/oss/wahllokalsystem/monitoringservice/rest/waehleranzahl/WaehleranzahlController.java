package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahlbeteiligung")
@RequiredArgsConstructor
@Slf4j
public class WaehleranzahlController extends AbstractController {

    private final WaehleranzahlService waehleranzahlService;
    private final WaehleranzahlDTOMapper waehleranzahlDTOMapper;

    @Operation(description = "Laden der zuvor gespeicherten Wahlbeteiligung für die Wahl {wahlID} für den Wahlbezirk {wahlbezirkID}.")
    @GetMapping("/{wahlID}/{wahlbezirkID}")
    ResponseEntity<WaehleranzahlDTO> getWahlbeteiligung(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        val waehleranzahlModel = waehleranzahlService.getWahlbeteiligung(new BezirkUndWahlID(wahlID, wahlbezirkID));

        return okWithBodyOrNoContent(waehleranzahlModel.map(waehleranzahlDTOMapper::toDTO));
    }

    @Operation(description = "Speichern und Weiterleiten der Wahlbeteiligung für die Wahl {wahlID} für den Wahlbezirk {wahlbezirkID}.")
    @PostMapping("/{wahlID}/{wahlbezirkID}")
    public void postWahlbeteiligung(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
        @RequestBody WaehleranzahlDTO waehleranzahl) {
        log.info("postWahlbeteiligung {}", wahlbezirkID);

        val waehleranzahlSetModel = waehleranzahlDTOMapper.toSetModel(new BezirkUndWahlID(wahlID, wahlbezirkID), waehleranzahl);

        waehleranzahlService.postWahlbeteiligung(waehleranzahlSetModel);
    }
}
