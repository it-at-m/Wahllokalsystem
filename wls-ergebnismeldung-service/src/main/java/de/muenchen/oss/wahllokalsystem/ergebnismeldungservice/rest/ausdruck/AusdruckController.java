package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/ausdruck")
@RequiredArgsConstructor
public class AusdruckController {

    private final AusdruckService ausdruckService;

    private final AusdruckReadDTOMapper ausdruckReadDTOMapper;

    private final AusdruckWriteDTOMapper ausdruckWriteDTOMapper;

    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<List<AusdruckReadDTO>> getAllAusdrucke(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        List<AusdruckReadDTO> res = ausdruckReadDTOMapper.fromListOfAusdruckModelToListOfAusdruckReadDTO(ausdruckService.getAll(wahlID, wahlbezirkID));
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public ResponseEntity<?> postAusdruck(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("meldungsart") final Meldungsart meldungsart,
            @RequestBody final AusdruckWriteDTO ausdruck) {
        AusdruckModel ausdruckModel = ausdruckWriteDTOMapper.toModel(ausdruck, new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart),
                Instant.now());
        ausdruckService.saveAusdruck(ausdruckModel);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public ResponseEntity<String> getAusdruck(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID,
            @PathVariable("meldungsart") Meldungsart meldungsart) {
        AusdruckReadDTO result = ausdruckReadDTOMapper.toDTO(ausdruckService.getAusdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart)));
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        val responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");

        return new ResponseEntity<>(result.content(), responseHeaders, HttpStatus.OK);
    }

}
