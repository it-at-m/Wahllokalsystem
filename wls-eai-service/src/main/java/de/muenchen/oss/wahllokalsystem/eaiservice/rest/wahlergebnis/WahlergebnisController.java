package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model.Ergebnismeldung;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlergebnis")
public class WahlergebnisController {

    @PostMapping
    public void saveWahlergebnismeldung(@Valid @RequestBody Ergebnismeldung ergebnismeldung) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
