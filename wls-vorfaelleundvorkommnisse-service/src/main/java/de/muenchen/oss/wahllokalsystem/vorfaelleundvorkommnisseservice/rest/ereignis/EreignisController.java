package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping(value = "/businessActions/ereignisse")
public class EreignisController {

    private final EreignisDTOMapper ereignisDTOMapper;

    private final EreignisService ereignisService;

    @Operation(description = "Laden der Ereignisse des Wahllokals {wahlbezirkID}")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlbezirkEreignisseDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Keine Daten vom Fremdsystem geliefert",
                            content = @Content(schema = @Schema())
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
    @GetMapping("/{wahlbezirkID}")
    public ResponseEntity<WahlbezirkEreignisseDTO> getEreignisse(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        val ereignisFromService = ereignisService.getEreignisse(wahlbezirkID);
        return okWithBodyOrNoContent(ereignisFromService.map(ereignisDTOMapper::toDTO));
    }

    @Operation(description = "Speichern der Ereignisse des Wahllokals {wahlbezirkID}")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlbezirkEreignisseDTO.class)) }
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
    @PostMapping("/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public void postEreignisse(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @RequestBody EreignisseWriteDTO ereignisseBody) {
        ereignisService.postEreignisse(ereignisDTOMapper.toModel(wahlbezirkID, ereignisseBody));
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
