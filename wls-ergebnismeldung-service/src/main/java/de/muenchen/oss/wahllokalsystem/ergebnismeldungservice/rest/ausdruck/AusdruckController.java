package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final ExceptionFactory exceptionFactory;

    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<List<AusdruckReadDTO>> getAllAusdrucke(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        List<AusdruckReadDTO> res = ausdruckReadDTOMapper.fromListOfAusdruckModelToListOfAusdruckReadDTO(ausdruckService.getAll(wahlID, wahlbezirkID));
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public ResponseEntity<?> postHtmlAusdruck(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("meldungsart") final Meldungsart meldungsart,
            @RequestBody final AusdruckWriteDTO ausdruck) {
        AusdruckModel ausdruckModel = ausdruckWriteDTOMapper.toModel(ausdruck, new WahlUndBezirkIDUndMeldungsart(wahlID, wahlbezirkID, meldungsart),
                Instant.now());
        ausdruckService.saveAusdruck(ausdruckModel);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public void getHtmlAusdruck(@PathVariable("wahlID") String wahlID,
            @PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("meldungsart") Meldungsart meldungsart,
            HttpServletResponse response) {
        AusdruckReadDTO result = ausdruckReadDTOMapper.toDTO(ausdruckService.getAusdruck(new WahlUndBezirkIDUndMeldungsart(wahlID, wahlbezirkID, meldungsart)));
        if (result == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        try {
            response.getWriter().print(result.content());
        } catch (IOException e) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG);
        }
    }

}
