package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/businessActions")
public class WahlvorstandController {

    private final String WAHLVORSTAND_PATH = "/wahlvorstand/{wahlbezirkID}";

    private final WahlvorstandService wahlvorstandService;
    private final WahlvorstandModelMapper wahlvorstandModelMapper;
    private final WahlvorstandDTOMapper wahlvorstandDTOMapper;

    @Operation(description = "Laden des Wahlvorstandes")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlvorstandDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @GetMapping(WAHLVORSTAND_PATH)
    public ResponseEntity<WahlvorstandDTO> getWahlvorstand(
            @RequestHeader(value = "forceupdate", required = false) String FORCE_UPDATE,
            @PathVariable("wahlbezirkID") String wahlbezirkID) {

        WahlvorstandModel result;

        if (FORCE_UPDATE != null && FORCE_UPDATE.equals("true")) {
            result = wahlvorstandService.updateWahlvorstand(wahlbezirkID);
        } else {
            result = wahlvorstandService.getWahlvorstand(wahlbezirkID);
        }
        if (result == null) {
            result = wahlvorstandService.getFallbackWahlvorstand(wahlbezirkID);
        }
        return okWithBodyOrNoContent(Optional.of(wahlvorstandDTOMapper.toDTO(result)));
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
