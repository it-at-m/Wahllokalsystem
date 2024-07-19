
package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen.ResetWahlenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/resetWahlen")
@RequiredArgsConstructor
@Slf4j
public class ResetWahlenController {

    private final ResetWahlenService resetWahlenService;

    @Operation(
            description = "Setzt die Attribute Farbe, Reihenfolge und Waehlerverzeichnis der vorhandenen Wahlen auf die Standardwerte.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahlen wurden zurückgesetzt."
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> resetWahlen() {
        resetWahlenService.resetWahlen();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
