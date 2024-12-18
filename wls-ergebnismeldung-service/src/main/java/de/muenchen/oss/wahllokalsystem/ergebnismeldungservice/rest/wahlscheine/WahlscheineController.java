package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
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
@RequestMapping("/businessActions/wahlscheine")
@RequiredArgsConstructor
public class WahlscheineController {

    private final WahlscheineService wahlscheineService;
    private final WahlscheineDTOMapper wahlscheineDTOMapper;

    @Operation(description = "Lesen der Anzahl an Stimmabgabevermerken mit Wahlschein eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existieren Stimmabgabevermerke",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlscheineDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Stimmabgabevermerke zu den entsprechenden Kriterien",
                            content = { @Content() }
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
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<WahlscheineDTO> getWahlscheine(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val wahlscheine = wahlscheineService.getWahlscheine(new BezirkUndWahlID(wahlID, wahlbezirkID));
        return okWithBodyOrNoContent(wahlscheine.map(wahlscheineDTOMapper::toDTO));
    }

    @Operation(description = "Setzen der Anzahl an Stimmabgabevermerken eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Stimmabgabevermerke erfolgreich gespeichert"
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
    @PostMapping("{wahlID}/{wahlbezirkID}")
    public void postWahlscheine(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final WahlscheineDTO wahlscheineDTO) {
        wahlscheineService.setWahlscheine(new BezirkUndWahlID(wahlID, wahlbezirkID), wahlscheineDTOMapper.toModel(wahlscheineDTO));
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final Optional<T> body) {
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
