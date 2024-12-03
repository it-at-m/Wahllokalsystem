package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
@Slf4j
public class AWerteController {

    private final AWerteService aWerteService;

    private final AWerteDTOMapper awerteDTOMapper;

    @Operation(description = "Laden der AWerte für den Wahlbezirk {wahlbezirkID}.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", description = "OK",
                content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AWerteDTO.class))) }
            ),
            @ApiResponse(
                responseCode = "204", description = "Keine Daten vom Fremdsystem geliefert",
                content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
            )
        }
    )
    @GetMapping("/awerte/{wahlbezirkID}")
    public ResponseEntity<List<AWerteDTO>> getAWerte(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        val result = awerteDTOMapper.fromListOfAWerteModelToListOfAWerteDTO(aWerteService.getAWerte(wahlbezirkID));
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(description = "Asynchrones initialisieren aller A-Werte für die gegebenen Wahlbezirk-IDs.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", description = "OK",
                content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
            )
        }
    )
    @PostMapping("/awerte/init")
    @ResponseStatus(HttpStatus.OK)
    public void initialiseAWerte(@RequestBody List<String> wahlbezirkIDs) {
        aWerteService.initialiseAWerte(wahlbezirkIDs);
    }
}
