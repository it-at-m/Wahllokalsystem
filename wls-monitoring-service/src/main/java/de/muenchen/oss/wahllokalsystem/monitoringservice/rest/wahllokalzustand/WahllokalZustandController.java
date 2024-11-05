package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandController {

    private final WahllokalZustandService wahllokalZustandService;

    @Operation(
            description = "Letzte Anwesenheit des Wahlbezirks {wahlbezirkID}. Generiert die Empfangs-Uhrzeit, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/lastSeen/{wahlbezirkID}")
    public void postLastSeen(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        log.info("postLastSeen {}", wahlbezirkID);
        wahllokalZustandService.postLastSeen(wahlbezirkID);
    }

    @Operation(
            description = "Empfängt die Request als Nachricht über die letzte Abmeldung des Wahlbezirks {wahlbezirkID}, generiert die Empfangs-Uhrzeit, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/letzteAbmeldung/{wahlbezirkID}")
    public void postLetzteAbmeldung(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        log.info("postLetzteAbmeldung {}", wahlbezirkID);
        wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID);
    }

    @Operation(
            description = "Empfängt die Daten über die Sendungsuhrzeit der Schnellmeldung, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/schnellmeldungSendungsuhrzeit")
    public void postSchnellmeldungsSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
        log.info("postSchnellmeldungsSendungsuhrzeit {}", sendungsdatenDTO);
        wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(sendungsdatenDTO.bezirkUndWahlID(), sendungsdatenDTO.sendungsuhrzeit());
    }

    @Operation(
            description = "Empfängt die Daten über die Druckuhrzeit der Schnellmeldung, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/schnellmeldungDruckuhrzeit")
    public void postSchnellmeldungDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
        log.info("postSchnellmeldungDruckuhrzeit {}", druckdatenDTO);
        wahllokalZustandService.postSchnellmeldungDruckuhrzeit(druckdatenDTO.bezirkUndWahlID(), druckdatenDTO.druckuhrzeit());
    }

    @Operation(
            description = "Empfängt die Daten über die Sendungsuhrzeit der Niederschrift, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/niederschriftSendungsuhrzeit")
    public void postNiederschriftSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
        log.info("postNiederschriftSendungsuhrzeit {}", sendungsdatenDTO);
        wahllokalZustandService.postNiederschriftSendungsuhrzeit(sendungsdatenDTO.bezirkUndWahlID(), sendungsdatenDTO.sendungsuhrzeit());
    }

    @Operation(
            description = "Empfängt die Daten über die Druckuhrzeit der Niederschrift, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter."
    )
    @PostMapping("/niederschriftDruckuhrzeit")
    public void postNiederschriftDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
        log.info("postNiederschriftDruckuhrzeit {}", druckdatenDTO);
        wahllokalZustandService.postNiederschriftDruckuhrzeit(druckdatenDTO.bezirkUndWahlID(), druckdatenDTO.druckuhrzeit());
    }
}
