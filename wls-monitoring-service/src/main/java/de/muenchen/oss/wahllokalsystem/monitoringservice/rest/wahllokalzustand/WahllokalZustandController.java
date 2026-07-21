package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandController {

  private final WahllokalZustandService wahllokalZustandService;

  @Operation(
      description =
          "Letzte Anwesenheit für das Team {teamID} des Wahlbezirks {wahlbezirkID}. Generiert die Empfangs-Uhrzeit, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Letzte Anwesenheit erfolgreich gespeichert.")
      })
  @PostMapping("/lastSeen")
  public void postLastSeen(
      @RequestParam("wahlbezirkID") @NotBlank(message = "Die wahlbezirkID darf nicht leer sein") final String wahlbezirkID,
      @RequestParam("teamID") @NotBlank(message = "Die teamID darf nicht leer sein") final String teamID) {
    log.info("postLastSeen Wahlbezirk: {} Team: {}", wahlbezirkID, teamID);
    wahllokalZustandService.postLastSeen(wahlbezirkID, teamID);
  }

  @Operation(
      description =
          "Empfängt die Request als Nachricht über die letzte Abmeldung für das Team {teamID} des Wahlbezirks {wahlbezirkID}, generiert die Empfangs-Uhrzeit, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Letzte Abmeldung erfolgreich weiter geleitet.")
      })
  @PostMapping("/letzteAbmeldung")
  public void postLetzteAbmeldung(
      @RequestParam("wahlbezirkID") final String wahlbezirkID,
      @RequestParam("teamID") final String teamID) {
    log.info("postLetzteAbmeldung Wahlbezirk: {} Team: {}", wahlbezirkID, teamID);
    wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID, teamID);
  }

  @Operation(
      description =
          "Empfängt die Daten über die Sendungsuhrzeit der Schnellmeldung, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Sendungsuhrzeit der Schnellmeldung erfolgreich weiter geleitet.")
      })
  @PostMapping("/schnellmeldungSendungsuhrzeit")
  public void postSchnellmeldungSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
    log.info("postSchnellmeldungSendungsuhrzeit {}", sendungsdatenDTO);
    wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(
        sendungsdatenDTO.bezirkUndWahlID(), sendungsdatenDTO.sendungsuhrzeit());
  }

  @Operation(
      description =
          "Empfängt die Daten über die Druckuhrzeit der Schnellmeldung, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Druckuhrzeit der Schnellmeldung erfolgreich weiter geleitet.")
      })
  @PostMapping("/schnellmeldungDruckuhrzeit")
  public void postSchnellmeldungDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
    log.info("postSchnellmeldungDruckuhrzeit {}", druckdatenDTO);
    wahllokalZustandService.postSchnellmeldungDruckuhrzeit(
        druckdatenDTO.bezirkUndWahlID(), druckdatenDTO.druckuhrzeit());
  }

  @Operation(
      description =
          "Empfängt die Daten über die Sendungsuhrzeit der Niederschrift, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Sendungsuhrzeit der Niederschrift erfolgreich weiter geleitet.")
      })
  @PostMapping("/niederschriftSendungsuhrzeit")
  public void postNiederschriftSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
    log.info("postNiederschriftSendungsuhrzeit {}", sendungsdatenDTO);
    wahllokalZustandService.postNiederschriftSendungsuhrzeit(
        sendungsdatenDTO.bezirkUndWahlID(), sendungsdatenDTO.sendungsuhrzeit());
  }

  @Operation(
      description =
          "Empfängt die Daten über die Druckuhrzeit der Niederschrift, packt diese in ein Wahllokalzustand-Objekt und leitet dieses weiter.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Druckuhrzeit der Niederschrift erfolgreich weiter geleitet.")
      })
  @PostMapping("/niederschriftDruckuhrzeit")
  public void postNiederschriftDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
    log.info("postNiederschriftDruckuhrzeit {}", druckdatenDTO);
    wahllokalZustandService.postNiederschriftDruckuhrzeit(
        druckdatenDTO.bezirkUndWahlID(), druckdatenDTO.druckuhrzeit());
  }
}
