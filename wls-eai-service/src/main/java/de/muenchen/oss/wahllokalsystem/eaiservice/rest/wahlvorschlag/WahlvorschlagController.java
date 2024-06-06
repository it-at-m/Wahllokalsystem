package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model.WahlvorschlaegeListe;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vorschlaege")
public class WahlvorschlagController {

    @GetMapping("referendum/{wahlID}/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public Referendumvorlagen loadReferendumvorlagen(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahl/{wahlID}/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public Wahlvorschlaege loadWahlvorschlaege(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahl/{wahlID}/liste")
    @ResponseStatus(HttpStatus.OK)
    public WahlvorschlaegeListe loadWahlvorschlaegeListe(@RequestParam("forDate") LocalDate wahltag, @PathVariable("wahlID") String wahlID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
