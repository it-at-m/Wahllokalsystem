package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahlenController {

    private final WahlenService wahlenService;

    private final WahlDTOMapper wahlenDTOMapper;

    @Operation(
            description = "Liest die Wahlen eines Wahltages vom BasisdatenService.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahlen wurden erfolgreich gelesen."
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existieren keine Wahlen zu den entsprechenden Kriterien.",
                            content = { @Content() }
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich.",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @GetMapping("/wahlen/{wahltagID}")
    public ResponseEntity<List<WahlDTO>> getWahlen(@PathVariable("wahltagID") final String wahltagID) {
        final List<WahlDTO> result = wahlenDTOMapper.toDtoList(wahlenService.getWahlen(wahltagID));
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @Operation(
            description = "Aktualisiert die Wahlen eines Wahltages im BasisdatenService.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahlen wurden erfolgreich gespeichert."
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich.",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @PostMapping("/wahlen/{wahltagID}")
    public ResponseEntity<?> updateWahlen(@RequestBody final List<WahlDTO> wahlen,
            @PathVariable("wahltagID") final String wahltagID) {
        wahlenService.updateWahlen(wahlenDTOMapper.toModelList(wahlen), wahltagID);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
