
package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/resetWahlen")
@RequiredArgsConstructor
@Slf4j
public class WahlenController {

    private final WahlenService wahlenService;

    @Operation(
            description = "Setzt die Attribute Farbe, Reihenfolge und Waehlerverzeichnis der vorhandenen Wahlen auf die Standardwerte.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahlen wurden zurückgesetzt."
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void resetWahlen() {
        wahlenService.resetWahlen();
    }
}
