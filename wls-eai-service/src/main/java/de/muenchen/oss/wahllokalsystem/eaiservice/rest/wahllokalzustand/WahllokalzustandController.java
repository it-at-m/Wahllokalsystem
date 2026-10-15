package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand.WahllokalZustandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/wahllokalzustand")
@RequiredArgsConstructor
public class WahllokalzustandController {

  private final WahllokalZustandService wahllokalZustandService;

  @Operation(
      description = "Speichert den Wahllokalzustand. ",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Wahllokalzustand erfolgreich gespeichert.")
      })
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public void saveWahllokalZustand(@Valid @RequestBody WahllokalZustandDTO wahllokalZustand) {
    wahllokalZustandService.setWahllokalZustand(wahllokalZustand);
  }

  @Operation(
      description = "Speichert den Wahllokalzustand lastSeen. ",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Wahllokalzustand erfolgreich gespeichert.")
      })
  @PostMapping("/lastSeen")
  @ResponseStatus(HttpStatus.OK)
  public void saveWahllokalZustandLastSeen(
      @RequestParam("wahlbezirkID") @NotBlank(message = "Die wahlbezirkID darf nicht leer sein") final String wahlbezirkID,
      @RequestParam("teamID") @NotBlank(message = "Die teamID darf nicht leer sein") final String teamID,
      @RequestParam("timestamp") @NotNull(message = "Der timestamp darf nicht leer sein") final LocalDateTime timestamp) {
    wahllokalZustandService.setWahllokalZustandLastSeen(wahlbezirkID, teamID, timestamp);
  }

  @Operation(
      description = "Speichert den Wahllokalzustand letzteAbmeldung. ",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Wahllokalzustand erfolgreich gespeichert.")
      })
  @PostMapping("/letzteAbmeldung")
  @ResponseStatus(HttpStatus.OK)
  public void saveWahllokalZustandLetzteAbmeldung(
      @RequestParam("wahlbezirkID") @NotBlank(message = "Die wahlbezirkID darf nicht leer sein") final String wahlbezirkID,
      @RequestParam("teamID") @NotBlank(message = "Die teamID darf nicht leer sein") final String teamID,
      @RequestParam("timestamp") @NotNull(message = "Der timestamp darf nicht leer sein") final LocalDateTime timestamp) {
    wahllokalZustandService.setWahllokalZustandLetzteAbmeldung(wahlbezirkID, teamID, timestamp);
  }
}
