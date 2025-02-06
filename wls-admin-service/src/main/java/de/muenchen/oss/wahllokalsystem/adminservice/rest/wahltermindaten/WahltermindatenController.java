package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahltermindatenController {

    private final WahltermindatenService wahltermindatenService;

    @Operation(
            description = "Importiert die Wahltermindaten.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahltermindaten wurden erfolgreich importiert."
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Fehler während des Imports",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @PostMapping("importWahltermindaten/{wahltagID}")
    @ResponseStatus(HttpStatus.OK)
    public void loadWahltermindaten(@PathVariable("wahltagID") String wahltagID) {
        wahltermindatenService.loadWahltermindaten(wahltagID);
    }

}
