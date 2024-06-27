package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahlvorschlaege")
@RequiredArgsConstructor
@Slf4j
public class WahlvorschlaegeController {

    private final WahlvorschlaegeService wahlvorschlaegeService;
    private final WahlvorschlaegeDTOMapper wahlvorschlaegeDTOMapper;

    @Operation(description = "Laden der Wahlvorschlaege des Wahllokals {wahlbezirkID} für die Wahl {wahlID}.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WahlvorschlaegeDTO.class)) }
                    )
            }
    )
    @GetMapping("/{wahlID}/{wahlbezirkID}")
    public WahlvorschlaegeDTO getWahlvorschlaege(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        return wahlvorschlaegeDTOMapper.toDTO(
                wahlvorschlaegeService.getWahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID)));
    }
}
