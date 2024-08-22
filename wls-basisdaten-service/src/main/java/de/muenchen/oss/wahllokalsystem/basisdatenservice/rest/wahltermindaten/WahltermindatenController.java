package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.WahltermindatenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
            description = "Triggert die Einrichtung der Wahltermindaten.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahltermindaten werden angelegt."
                    )
            }
    )
    @PutMapping("wahltermindaten/{wahltagID}")
    @ResponseStatus(HttpStatus.OK)
    public void putWahltermindaten(@PathVariable("wahltagID") String wahltagID) {
        wahltermindatenService.putWahltermindaten(wahltagID);
    }

    @Operation(
            description = "Löscht die Wahltermindaten eines bestimmten Wahltags.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahltermindaten werden gelöscht."
                    )
            }
    )
    @DeleteMapping("wahltermindaten/{wahltagID}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWahltermindaten(@PathVariable("wahltagID") String wahltagID) {
        wahltermindatenService.deleteWahltermindaten(wahltagID);
    }
}
