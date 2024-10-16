package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/referendumvorlagen")
@RequiredArgsConstructor
public class ReferendumvorlagenController {

    private final ReferendumvorlagenService referendumvorlagenService;

    private final ReferendumvorlagenDTOMapper referendumvorlagenDTOMapper;

    @Operation(description = "Laden der Referendumsvorlagen des Wahllokals {wahlbezirkID} für eine Wahl {wahlID}.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ReferendumvorlagenDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Kommunikation mit dem externen System von dem die Daten importiert werden",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Keine Daten vom Fremdsystem geliefert",
                            content = @Content(schema = @Schema())
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ReferendumvorlagenDTO getReferendumvorlagen(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        return referendumvorlagenDTOMapper.toDTO(
                referendumvorlagenService.getReferendumvorlagen(referendumvorlagenDTOMapper.toModel(wahlbezirkID, wahlID)));
    }
}
