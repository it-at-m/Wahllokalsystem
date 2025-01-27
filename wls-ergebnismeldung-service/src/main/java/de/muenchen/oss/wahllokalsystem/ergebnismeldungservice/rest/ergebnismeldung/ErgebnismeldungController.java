package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/sendErgebnismeldung")
@RequiredArgsConstructor
public class ErgebnismeldungController {

    private final ErgebnismeldungService ergebnismeldungService;

    @PostMapping("{wahlID}/{wahlbezirkID}/{waehlerverzeichnisNummer}/{meldungsart}/{hauptwahlbezirkID}")
    public ResponseEntity<?> sendErgebnisse(
            @RequestHeader(required = false, name = "forceergebnismeldung") final String forceUpdate,
            @PathVariable("wahlID") String wahlID,
            @PathVariable("wahlbezirkID") String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") Long waehlerverzeichnisNummer,
            @PathVariable("meldungsart") MeldungsartDTO meldungsart,
            @PathVariable("hauptwahlbezirkID") String hauptwahlbezirkID) {
        return ResponseEntity.ok().build();
    }
}


