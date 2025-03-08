package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenService;
import io.swagger.v3.oas.annotations.Operation;
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
                            responseCode = "200", description = "Referendumvorlage erfolgreich zurückgegeben"
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ReferendumvorlagenDTO getReferendumvorlagen(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        return referendumvorlagenDTOMapper.toDTO(
                referendumvorlagenService.getReferendumvorlagen(referendumvorlagenDTOMapper.toModel(wahlbezirkID, wahlID)));
    }
}
