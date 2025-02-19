package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage.WahltageService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahltageController {

    private final WahltageService wahltageService;

    private final WahltagDTOMapper wahltagDTOMapper;

    @Operation(
        description = "Liest alle vorhandenen Wahltage vom BasisdatenService.",
        responses = {
            @ApiResponse(
                responseCode = "200", description = "Die Wahltage wurden erfolgreich gelesen."
            ),
            @ApiResponse(
                responseCode = "204", description = "Es existieren keine Wahltage.",
                content = { @Content() }
            ),
            @ApiResponse(
                responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
            )
        }
    )
    @GetMapping("/wahltage")
    public ResponseEntity<List<WahltagDTO>> getWahltage() {
        final List<WahltagDTO> result = wahltagDTOMapper.toDtoList(wahltageService.getWahltage());
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

}
