package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model.Ergebnismeldung;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlergebnis")
public class WahlergebnisController {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveWahlergebnismeldung(@Valid @RequestBody Ergebnismeldung ergebnismeldung) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
