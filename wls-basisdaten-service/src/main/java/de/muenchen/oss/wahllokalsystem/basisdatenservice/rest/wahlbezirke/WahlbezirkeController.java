package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahlbezirke")
@RequiredArgsConstructor
@Slf4j
public class WahlbezirkeController {

    private final WahlbezirkeService wahlbezirkeService;
    private final WahlbezirkDTOMapper wahlbezirkDTOMapper;

    @Operation(description = "Laden der Liste der Wahlbezirke, die einem vorgegebenen Wahltag (Parameter wahltagID) entsprechen.", responses = {
            @ApiResponse(
                    responseCode = "200", description = "Wahlbezirke erfolgreich zurückgegeben."
            )
    })
    @GetMapping("/{wahltagID}")
    public List<WahlbezirkDTO> getWahlbezirke(@PathVariable("wahltagID") final String wahltagID) {
        return wahlbezirkDTOMapper.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(wahlbezirkeService.getWahlbezirke(wahltagID));
    }
}
