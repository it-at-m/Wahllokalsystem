package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model.Wahlvorstandsaktualisierung;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlvorstaende")
public class WahlvorstandController {

    @GetMapping
    public Wahlvorstand loadWahlvorstand(@RequestParam("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @PostMapping
    public void saveAnwesenheit(@Valid @RequestBody Wahlvorstandsaktualisierung wahlvorstand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
