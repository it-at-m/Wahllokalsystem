package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine.WahlscheineDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/businessActions/ergebnisse")
@RequiredArgsConstructor
public class ErgebnisseController {

    private final ErgebnisseService ergebnisseService;

    private final ErgebnisseDTOMapper ergebnisseDTOMapper;

    @Operation(description = "Lesen von Ergebnissen von einem Wahlbezirk für eine Wahl auf einem bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existieren Ergebnisse",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlscheineDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Ergebnisse zu den entsprechenden Kriterien",
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
    @GetMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    public ResponseEntity<ErgebnisseDTO> getErgebnisse(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") Stapelart stapelart) {
        val referenceModel = ergebnisseDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart);
        val ergebnisseFromService = ergebnisseDTOMapper.toDTO(ergebnisseService.getErgebnisse(referenceModel));

        return okWithBodyOrNoContent(ergebnisseFromService);
    }

    @Operation(description = "Setzen von Ergebnissen von einem Wahlbezirk für eine Wahl auf einem bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Ergebnisse erfolgreich gespeichert"
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
    @PostMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    @ResponseStatus(HttpStatus.OK)
    public void postErgebnisse(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") Stapelart stapelart,
            @RequestBody ErgebnisseDTO ergebnisseDTO) {
        val modelToSave = ergebnisseDTOMapper.toModel(ergebnisseDTO);
        val referenceForModel = new ErgebnisseReference(wahlbezirkID, wahlID, stapelart);
        ergebnisseService.postErgebnisse(modelToSave, referenceForModel);
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
