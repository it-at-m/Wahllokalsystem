package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung.WahlbeteiligungService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahlbeteiligung")
@RequiredArgsConstructor
public class WahlbeteiligungController {

    private final WahlbeteiligungService wahlbeteiligungService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveWahlbeteiligung(@RequestBody WahlbeteiligungsMeldungDTO wahlbeteiligung) {
        wahlbeteiligungService.saveWahlbeteiligung(wahlbeteiligung);
    }
}
