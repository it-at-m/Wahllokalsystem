package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung.ErgebnismeldungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ergebnismeldung")
@RequiredArgsConstructor
public class WahlergebnisController {

    private final ErgebnismeldungService ergebnismeldungService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveErgebnismeldung(@Valid @RequestBody ErgebnismeldungDTO ergebnismeldung) {
        ergebnismeldungService.saveErgebnismeldung(ergebnismeldung);
    }
}
