package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
@RequestMapping("/businessActions/stimmabgabevermerke")
@RequiredArgsConstructor
public class StimmabgabevermerkeController {

    private final StimmabgabevermerkeService stimmabgabevermerkeService;
    private final StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;



    @Operation(description = "Lesen der Stimmabgabevermerke eines Wahlbezirkes für die Wahl(en) eines Wählerverzeichnisses.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existierten Stimmabgabevermerke.",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StimmabgabevermerkeDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Stimmabgabevermerke entsprechend der Such-Kriterien.",
                            content = { @Content() }
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich.",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage.",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @GetMapping("{wahlbezirkID}/{waehlerverzeichnisNummer}")
    public ResponseEntity<StimmabgabevermerkeDTO> getStimmabgabevermerke(
            @PathVariable("wahlbezirkID") String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") Long waehlerverzeichnisNummer) {
        val stimmabgabevermerke = stimmabgabevermerkeService.getStimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer));
        return okWithBodyOrNoContent(stimmabgabevermerke.map(stimmabgabevermerkeDTOMapper::toStimmabgabevermerkeDTO));
    }

    @Operation(description = "Speichern der Stimmabgabevermerke eines Wahlbezirkes für die Wahl(en) eines Wählerverzeichnisses.")
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
    public void postStimmabgabevermerke(
            @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") final long waehlerverzeichnisNummer,
            @RequestBody final StimmabgabevermerkeDTO stimmabgabevermerkeDTO) {
        stimmabgabevermerkeService.postStimmabgabevermerke(
                new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeModel(stimmabgabevermerkeDTO)
        );
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final Optional<T> body) {
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
