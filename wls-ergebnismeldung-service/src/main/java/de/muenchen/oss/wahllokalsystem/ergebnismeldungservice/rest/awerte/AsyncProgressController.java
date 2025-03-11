package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/asyncProgress")
@RequiredArgsConstructor
public class AsyncProgressController {

    private final AsyncProgressDTOMapper asyncProgressDTOMapper;
    private final AsyncProgress asyncProgress;

    @Operation(description = "Lesen des Bearbeitungsstandes der Initialisierung der AWerte.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Auskunft über Bearbeitungszustand erhalten",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AsyncProgressDTO.class)) }
                    )
            }
    )
    @GetMapping
    public AsyncProgressDTO getAsyncProgress() {
        return asyncProgressDTOMapper.toDTO(asyncProgress);
    }
}
