package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.WahlvorstandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlvorstaende")
@RequiredArgsConstructor
public class WahlvorstandController {

    private final WahlvorstandService wahlvorstandService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('EAI_Wahlvorstaende_LoadWahlvorstand')")
    public WahlvorstandDTO loadWahlvorstand(final @RequestParam("wahlbezirkID") String wahlbezirkID) {
        return wahlvorstandService.getWahlvorstandForWahlbezirk(wahlbezirkID);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('EAI_Wahlvorstaende_SaveAnwesenheit')")
    public void saveAnwesenheit(@Valid @RequestBody WahlvorstandsaktualisierungDTO wahlvorstand) {
        wahlvorstandService.setAnwesenheit(wahlvorstand);
    }
}
