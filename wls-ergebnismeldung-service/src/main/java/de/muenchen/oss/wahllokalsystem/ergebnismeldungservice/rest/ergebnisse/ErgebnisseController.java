package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
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
public class ErgebnisseController extends AbstractController {

    private final ErgebnisseService ergebnisseService;

    private final ErgebnisseDTOMapper ergebnisseDTOMapper;

    @Operation(description = "Lesen von Ergebnissen eines Wahlbezirks für eine Wahl von einem bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existieren Ergebnisse",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErgebnisseDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Ergebnisse zu den entsprechenden Kriterien",
                            content = { @Content() }
                    )
            }
    )
    @GetMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    public ResponseEntity<ErgebnisseDTO> getErgebnisse(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") StapelartDTO stapelart) {
        val stapelartToUseInReference = ergebnisseDTOMapper.toSpapelart(stapelart);
        val ergebnisse = ergebnisseService.getErgebnisse(new ErgebnisseReference(wahlbezirkID, wahlID, stapelartToUseInReference));
        return okWithBodyOrNoContent(ergebnisse.map(ergebnisseDTOMapper::toDTO));
    }

    @Operation(description = "Lesen von allen Ergebnissen eines Wahlbezirks für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existieren Ergebnisse",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErgebnisseDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Ergebnisse zu den entsprechenden Kriterien",
                            content = { @Content() }
                    )
            }
    )
    @GetMapping("{wahlbezirkID}/{wahlID}")
    public ResponseEntity<List<ErgebnisseDTO>> getAllErgebnisse(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID) {
        val ergebnisse = ergebnisseService.getAllErgebnisse(wahlbezirkID, wahlID);
        if (ergebnisse.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        val ergebnisseDTOList = ergebnisse.stream().map(ergebnisseDTOMapper::toDTO).toList();
        return ResponseEntity.ok(ergebnisseDTOList);
    }

    @Operation(description = "Setzen von Ergebnissen eines Wahlbezirks für eine Wahl für einen bestimmten Stapel")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Ergebnisse erfolgreich gespeichert"
                    )
            }
    )
    @PostMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    @ResponseStatus(HttpStatus.OK)
    public void postErgebnisse(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") StapelartDTO stapelart,
            @RequestBody ErgebnisseDTO ergebnisseDTO) {
        val stapelartToUseInReference = ergebnisseDTOMapper.toSpapelart(stapelart);
        val modelToSave = ergebnisseDTOMapper.toModel(ergebnisseDTO);
        val referenceForModel = new ErgebnisseReference(wahlbezirkID, wahlID, stapelartToUseInReference);
        ergebnisseService.postErgebnisse(referenceForModel, modelToSave);
    }
}
