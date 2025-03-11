package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final AusdruckDTOMapper ausdruckDTOMapper;

    @Operation(description = "Lesen eines Ausdrucks einer bestimmten Meldungsart für einen Wahlbezirk einer Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existiert ein Ausdruck",
                            content = { @Content(mediaType = "text/html; charset=utf-8", schema = @Schema(implementation = AusdruckReadDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existiert kein Ausdruck zu den entsprechenden Kriterien",
                            content = { @Content() }
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public ResponseEntity<String> getAusdruck(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @PathVariable("meldungsart") final Meldungsart meldungsart) {
        val ausdruckReadModel = ausdruckService.getAusdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart));

        if (ausdruckReadModel.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            val result = ausdruckDTOMapper.toDTO(ausdruckReadModel.get());
            val responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
            return new ResponseEntity<>(result.content(), responseHeaders, HttpStatus.OK);
        }
    }

    @Operation(description = "Lesen aller Ausdrucke für einen Wahlbezirk einer Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existiert ein Ausdruck",
                            content = {
                                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AusdruckReadDTO.class))) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existiert kein Ausdruck zu den entsprechenden Kriterien",
                            content = { @Content() }
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<List<AusdruckReadDTO>> getAllAusdrucke(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val ausdrucke = ausdruckService.getAllAusdrucke(wahlID, wahlbezirkID);
        return ResponseEntity.ok(ausdrucke.stream().map(ausdruckDTOMapper::toDTO).toList());
    }

    @Operation(description = "Speichern eines Ausdrucks einer bestimmten Meldungsart für einen Wahlbezirk einer Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Ausdruck erfolgreich gespeichert"
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @PostMapping("{wahlID}/{wahlbezirkID}/{meldungsart}/html")
    public ResponseEntity<?> postAusdruck(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("meldungsart") final Meldungsart meldungsart,
            @RequestBody final AusdruckWriteDTO ausdruck) {
        val ausdruckWriteModel = ausdruckDTOMapper.toModel(ausdruck, new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart));
        ausdruckService.saveAusdruck(ausdruckWriteModel);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
