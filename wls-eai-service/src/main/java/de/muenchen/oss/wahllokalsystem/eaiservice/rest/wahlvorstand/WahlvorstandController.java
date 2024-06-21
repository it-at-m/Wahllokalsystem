package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlvorstaende")
@RequiredArgsConstructor
public class WahlvorstandController {

    private final WahlvorstandService wahlvorstandService;

    @GetMapping
    @Operation(description = "Abrufen des Wahlvorstandes für einen bestimmten Wahlbezirk")
    public WahlvorstandDTO loadWahlvorstand(final @RequestParam("wahlbezirkID")
    String wahlbezirkID) {
        return wahlvorstandService.getWahlvorstandForWahlbezirk(wahlbezirkID);
    }

    @PutMapping("anwesenheit")
    @Operation(description = "Aktualisieren der Anwesenheit der Wahlvorstandsmitglieder eines bestimmten Wahlbezirkes")
    public void saveAnwesenheit(@RequestBody WahlvorstandsaktualisierungDTO wahlvorstand) {
        wahlvorstandService.setAnwesenheit(wahlvorstand);
    }
}
