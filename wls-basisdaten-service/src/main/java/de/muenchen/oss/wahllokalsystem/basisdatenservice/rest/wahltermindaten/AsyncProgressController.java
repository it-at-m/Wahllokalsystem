package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/asyncProgress")
@RequiredArgsConstructor
public class AsyncProgressController {

    private final AsyncProgress asyncProgress;
    private final AsyncProgressDTOMapper asyncProgressDTOMapper;

    @Operation(
            description = "Abrufen des Fortschrittes bei der Importierung der Wahltermindaten.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Importfortschritt wurde geliefert."
                    )
            }
    )
    @GetMapping
    public AsyncProgressDTO getAsyncProgress() {
        return asyncProgressDTOMapper.toDto(asyncProgress);
    }
}
