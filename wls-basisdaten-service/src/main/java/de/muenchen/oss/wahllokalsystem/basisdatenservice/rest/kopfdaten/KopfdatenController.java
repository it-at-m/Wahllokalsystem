package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/kopfdaten")
@RequiredArgsConstructor
@Slf4j
public class KopfdatenController {

    private final KopfdatenService kopfdatenService;
    private final KopfdatenDTOMapper kopfdatenDTOMapper;

    @Operation(description = "Laden der Kopfdaten für die Wahl {wahlID} für den Wahlbezirk {wahlbezirkID}.")
    @GetMapping("/{wahlID}/{wahlbezirkID}")
    public KopfdatenDTO getKopfdaten(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        return kopfdatenDTOMapper.toDTO(
                kopfdatenService.getKopfdaten(new BezirkUndWahlID(wahlID, wahlbezirkID)));
    }

}
