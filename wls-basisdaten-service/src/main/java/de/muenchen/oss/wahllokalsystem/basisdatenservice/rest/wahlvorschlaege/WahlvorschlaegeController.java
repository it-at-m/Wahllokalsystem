package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahlvorschlaege")
@RequiredArgsConstructor
@Slf4j
public class WahlvorschlaegeController {

    private final WahlvorschlaegeService wahlvorschlaegeService;

    @Operation(description = "Laden der Wahlvorschlaege des Wahllokals {wahlbezirkID} für die Wahl {wahlID}.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WLSWahlvorschlaegeDTO.class)) }
                    )
            }
    )
    @GetMapping("/{wahlID}/{wahlbezirkID}")
    public ResponseEntity<JSONObject> getWahlvorschlaege(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {

        String result = wahlvorschlaegeService.getWahlvorschlaege(wahlID, wahlbezirkID);

        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(result);
        } catch (ParseException e) {
            log.error("JSON parse Exception, " + e);
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
