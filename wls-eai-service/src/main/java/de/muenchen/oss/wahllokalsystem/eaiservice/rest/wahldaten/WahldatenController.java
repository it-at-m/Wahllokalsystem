package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WahldatenController.WAHLDATEN_REQUEST_MAPPING)
@RequiredArgsConstructor
public class WahldatenController {

    public static final String WAHLDATEN_REQUEST_MAPPING = "/wahldaten";

    private final WahldatenService wahldatenService;

    @Operation(
            description = "Sucht nach allen Wahlberechtigten in einem Wahlbezirk { wahlbezirkId } und gibt diese zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste aller Wahlberechtigten erfolgreich zurückgegeben.")
            }
    )
    @GetMapping("wahlbezirke/{wahlbezirkID}/wahlberechtigte")
    @ResponseStatus(HttpStatus.OK)
    public List<WahlberechtigteDTO> loadWahlberechtigte(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        return wahldatenService.getWahlberechtigte(wahlbezirkID);
    }

    @Operation(
            description = "Sucht alle Wahltage seit einem bestimmten wahltag, der wahltag wird bei der suche inkludiert.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste aller Wahltage erfolgreich zurückgegeben.")
            }
    )
    @GetMapping("wahltage")
    @ResponseStatus(HttpStatus.OK)
    public Set<WahltagDTO> loadWahltageSinceIncluding(@RequestParam("includingSince") LocalDate tag) {
        return wahldatenService.getWahltage(tag);
    }

    @Operation(
            description = "Sucht alle Wahlbezirke für einen bestimmten Wahltag und eine Nummer. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste aller Wahlbezirke erfolgreich zurückgegeben.")
            }
    )
    @GetMapping("wahlbezirk")
    @ResponseStatus(HttpStatus.OK)
    public Set<WahlbezirkDTO> loadWahlbezirke(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        return wahldatenService.getWahlbezirke(wahltag, nummer);
    }

    @Operation(
            description = "Sucht alle Wahlen für einen bestimmten Wahltag und eine Nummer. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste aller Wahlen erfolgreich zurückgegeben.")
            }
    )
    @GetMapping("wahlen")
    @ResponseStatus(HttpStatus.OK)
    public Set<WahlDTO> loadWahlen(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        return wahldatenService.getWahlen(wahltag, nummer);
    }

    @Operation(
            description = "Sucht alle Basisdaten für einen bestimmten Wahltag und eine Nummer. ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste aller Basisdaten erfolgreich zurückgegeben.")
            }
    )
    @GetMapping("basisdaten")
    @ResponseStatus(HttpStatus.OK)
    public BasisdatenDTO loadBasisdaten(@RequestParam("forDate") LocalDate wahltag, @RequestParam("withNummer") String nummer) {
        return wahldatenService.getBasisdaten(wahltag, nummer);
    }
}
