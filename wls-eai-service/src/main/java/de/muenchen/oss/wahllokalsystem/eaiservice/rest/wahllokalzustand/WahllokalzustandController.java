package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand.WahllokalZustandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
