package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
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

/**
 * This controller is responsible for mapping businessActions to REST-endpoints.
 */
@RestController
@RequestMapping(value = "/businessActions")
public class EreignisController {

    private final EreignisDTOMapper ereignisDTOMapper;

    EreignisService ereignisService;

    public EreignisController(EreignisDTOMapper ereignisDTOMapper, EreignisService ereignisService) {
        this.ereignisDTOMapper = ereignisDTOMapper;
        this.ereignisService = ereignisService;
    }

    /**
     * This BusinessAction's purpose is: Laden der besonderen Ereignisse
     * It returns one Ereignis.
     */
    @Operation(description = "Laden der Ereignisse des Wahllokals {wahlbezirkID}")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EreignisDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Kommunikation mit dem externen System von dem die Daten importiert werden",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Keine Daten vom Fremdsystem geliefert",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("/ereignisse/{wahlbezirkID}")
    public ResponseEntity<EreignisDTO> getEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        val ereignisFromService = ereignisService.getEreignis(wahlbezirkID);
        return withBodyOrNoContent(ereignisFromService.map(ereignisDTOMapper::toDTO));
    }

    /**
     * This BusinessAction's purpose is: Speichern von besonderen Ereignissen
     */
    @Operation(description = "Speichern der Ereignisse des Wahllokals {wahlbezirkID}")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EreignisDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Kommunikation mit dem externen System von dem die Daten importiert werden",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @PostMapping("/ereignisse/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public void postEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @RequestBody EreignisWriteDTO ereignisBody) {
        ereignisService.postEreignis(ereignisDTOMapper.toModel(wahlbezirkID, ereignisBody));
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
