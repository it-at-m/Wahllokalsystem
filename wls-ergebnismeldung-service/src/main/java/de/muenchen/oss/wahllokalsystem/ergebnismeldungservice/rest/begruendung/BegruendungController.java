package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine.WahlscheineDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungService;
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
@RequestMapping("/businessActions/begruendung")
@RequiredArgsConstructor
public class BegruendungController {

    private final BegruendungService begruendungService;

    private final BegruendungDTOMapper begruendungDTOMapper;

    @Operation(description = "Lesen der Begruendung einer Meldung von einem Wahlbezirk für eine Wahl auf einem bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existiert eine Begruendung",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlscheineDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Begruendungen zu den entsprechenden Kriterien",
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
    public ResponseEntity<BegruendungDTO> getBegruendung(@PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("wahlID") final String wahlID,
            @PathVariable("stapelart") final Stapelart stapelart) {
        val referenceModel = begruendungDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart);
        val begruendungFromService = begruendungDTOMapper.toDTO(begruendungService.getBegruendung(referenceModel));

        return okWithBodyOrNoContent(begruendungFromService);
    }

    @Operation(description = "Setzen der Begruendung einer Meldung von einem Wahlbezirk für eine Wahl auf einem bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Begruendung erfolgreich gespeichert"
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
    public void postBegruendung(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") Stapelart stapelart,
            @RequestBody BegruendungDTO begruendungDTO) {
        val modelToSave = begruendungDTOMapper.toModel(begruendungDTO);
        val referenceForModel = new BegruendungReference(wahlbezirkID, wahlID, stapelart);
        begruendungService.postBegruendung(modelToSave, referenceForModel);
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
