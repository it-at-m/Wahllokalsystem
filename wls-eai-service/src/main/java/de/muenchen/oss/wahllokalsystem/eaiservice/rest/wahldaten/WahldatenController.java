package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model.Basisdaten;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model.Wahlberechtigte;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model.Wahlbezirke;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model.Wahlen;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model.Wahltage;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahldaten")
public class WahldatenController {

    @GetMapping("wahlbezirke/{wahlbezirkID}/wahlberechtigte")
    @ResponseStatus(HttpStatus.OK)
    public List<Wahlberechtigte> loadWahlberechtigte(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahltage")
    @ResponseStatus(HttpStatus.OK)
    public Wahltage loadWahltageSinceIncluding(@RequestParam("includingAfter") LocalDate tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahlbezirk")
    @ResponseStatus(HttpStatus.OK)
    public Wahlbezirke loadWahlbezirke(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahlen")
    @ResponseStatus(HttpStatus.OK)
    public Wahlen loadWahlen(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("basisdaten")
    @ResponseStatus(HttpStatus.OK)
    public Basisdaten getBasisdaten(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
