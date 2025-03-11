package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/stimmzettelumschlaege")
@RequiredArgsConstructor
public class StimmzettelumschlaegeController extends AbstractController {

    private final StimmzettelumschlaegeService stimmzettelumschlaegeService;
    private final StimmzettelumschlaegeDTOMapper stimmzettelumschlaegeDTOMapper;

    @Operation(description = "Lesen der Anzahl an Stimmzettelumschlaegen eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existiert eine Anzahl an Stimmzettelumschlaegen",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StimmzettelumschlaegeDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existiert keine Anzahl an Stimmzettelumschlaegen zu den entsprechenden Kriterien",
                            content = { @Content() }
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<StimmzettelumschlaegeDTO> getStimmzettelumschlaege(@PathVariable("wahlID") final String wahlID,
            @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val stimmzettelumschlaege = stimmzettelumschlaegeService.getStimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID));
        return okWithBodyOrNoContent(stimmzettelumschlaege.map(stimmzettelumschlaegeDTOMapper::toDTO));
    }

    @Operation(description = "Setzen der Anzahl an Stimmzettelumschlaegen eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Anzahl an Stimmzettelumschlaegen erfolgreich gespeichert"
                    )
            }
    )
    @PostMapping("{wahlID}/{wahlbezirkID}")
    public void postStimmzettelumschlaege(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final StimmzettelumschlaegeDTO stimmzettelumschlaegeDTO) {
        stimmzettelumschlaegeService.setStimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID),
                stimmzettelumschlaegeDTOMapper.toModel(stimmzettelumschlaegeDTO));
    }
}
