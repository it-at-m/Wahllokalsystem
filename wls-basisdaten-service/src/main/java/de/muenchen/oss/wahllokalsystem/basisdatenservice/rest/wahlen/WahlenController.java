package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahlenController {

    private final WahlenService wahlenService;

    private final WahlDTOMapper wahlDTOMapper;

    @Operation(description = "Laden der Wahlen des Wahltages { wahltagID }.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WahlDTO.class))) }
                    )
            }
    )
    @GetMapping("wahlen/{wahltagID}")
    public List<WahlDTO> getWahlen(@PathVariable("wahltagID") String wahltagID) {
        return wahlDTOMapper.fromListOfWahlModelToListOfWahlDTO(wahlenService.getWahlen(wahltagID));
    }

    @PostMapping("wahlen/{wahltagID}")
    @Operation(
            description = "Speichern einer Liste von Wahlen.",
            responses = {
                    @ApiResponse(responseCode = "500", description = "Wahlen können nicht gespeichert werden."),
                    @ApiResponse(
                            responseCode = "400", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public void postWahlen(@PathVariable("wahltagID") String wahltagID, @RequestBody List<WahlDTO> wahlDTOs) {
        wahlenService.postWahlen(wahltagID, wahlDTOMapper.fromListOfWahlDTOtoListOfWahlModel(wahlDTOs));
    }

    @Operation(
            description = "Setzt die Attribute Farbe, Reihenfolge und Waehlerverzeichnis der vorhandenen Wahlen auf die Standardwerte.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die Wahlen wurden zurückgesetzt."
                    )
            }
    )
    @PostMapping("resetWahlen/")
    @ResponseStatus(HttpStatus.OK)
    public void resetWahlen() {
        wahlenService.resetWahlen();
    }

}
