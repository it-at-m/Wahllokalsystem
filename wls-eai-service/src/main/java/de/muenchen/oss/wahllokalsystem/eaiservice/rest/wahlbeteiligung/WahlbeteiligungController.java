package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.model.WahlbeteiligungsMeldung;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlbeteiligung")
public class WahlbeteiligungController {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveWahlbeteiligung(@Valid @RequestBody WahlbeteiligungsMeldung wahlbeteiligung) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
