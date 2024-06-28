package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahldaten")
@RequiredArgsConstructor
public class WahldatenController {

    private final WahldatenService wahldatenService;

    @GetMapping("wahlbezirke/{wahlbezirkID}/wahlberechtigte")
    @ResponseStatus(HttpStatus.OK)
    public List<WahlberechtigteDTO> loadWahlberechtigte(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahltage")
    @ResponseStatus(HttpStatus.OK)
    public Set<WahltagDTO> loadWahltageSinceIncluding(@RequestParam("includingSince") LocalDate tag) {
        return wahldatenService.getWahltage(tag);
    }

    @GetMapping("wahlbezirk")
    @ResponseStatus(HttpStatus.OK)
    public WahlbezirkeDTO loadWahlbezirke(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahlen")
    @ResponseStatus(HttpStatus.OK)
    public WahlenDTO loadWahlen(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("basisdaten")
    @ResponseStatus(HttpStatus.OK)
    public BasisdatenDTO loadBasisdaten(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
