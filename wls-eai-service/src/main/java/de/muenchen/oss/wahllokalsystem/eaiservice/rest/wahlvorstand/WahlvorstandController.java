package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlvorstaende")
public class WahlvorstandController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public WahlvorstandDTO loadWahlvorstand(@RequestParam("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveAnwesenheit(@Valid @RequestBody WahlvorstandsaktualisierungDTO wahlvorstand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
