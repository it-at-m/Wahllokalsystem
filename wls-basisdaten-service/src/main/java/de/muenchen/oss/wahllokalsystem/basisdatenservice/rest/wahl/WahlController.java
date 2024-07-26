package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahlvorschlaege")
@RequiredArgsConstructor
@Slf4j
public class WahlController {

    private final WahlService wahlService;

    private final WahlDTOMapper wahlDTOMapper;

    private final ExceptionFactory exceptionFactory;

    @Operation(description = "Laden der Wahlen des Wahl {wahlID}.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlDTO.class)) }
                    )
            }
    )
    @GetMapping("/{wahlID}")
    public WahlDTO getWahlen(@PathVariable("wahlID") String wahlID) {
        return wahlDTOMapper.toDTO(
                wahlService.getWahlen(wahlID);
    }

    public ResponseEntity<?> postWahlen(@RequestHeader Map<String, Object> headers, @PathVariable("wahltagID") String wahltagID,
                                        @RequestBody List<Wahl> wahlen) {
        postWahlenactionService.postWahlen(headers, wahltagID, wahlen);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("{wahlID}")
    @Operation(
            description = "Speichern einer Wahl",
            responses = {
                    @ApiResponse(responseCode = "500", description = "Wahl kann nicht gespeichert werden"),
                    @ApiResponse(
                            responseCode = "400", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public void postWahl(@PathVariable("wahlID") String wahlID,
                                   @RequestBody WahlDTO wahlDTO) {
        wahlService.setWahl(wahlDTOMapper.toModel(wahlDTO));
    }

}
