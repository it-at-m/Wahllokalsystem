package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahllokalzustand")
public class WahllokalzustandController {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveWahllokalZustand(@Valid @RequestBody WahllokalZustandDTO wahllokalZustand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
