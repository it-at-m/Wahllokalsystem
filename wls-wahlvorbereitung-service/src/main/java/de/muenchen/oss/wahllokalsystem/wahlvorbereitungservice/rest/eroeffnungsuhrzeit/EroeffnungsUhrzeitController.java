package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/eroeffnungsuhrzeit")
@RequiredArgsConstructor
public class EroeffnungsUhrzeitController extends AbstractController {

    private final EroeffnungsUhrzeitService eroeffnungsuhrzeitService;
    private final EroeffnungsUhrzeitDTOMapper EroeffnungsuhrzeitDTOMapper;

    @Operation(description = "Laden der Eroeffnungsuhrzeit des Wahllokals {wahlbezirkID}")
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<EroeffnungsUhrzeitDTO> getEroeffnungsuhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val eroeffnungsuhrzeitModel = eroeffnungsuhrzeitService.getEroeffnungsUhrzeit(wahlbezirkID);

        return okWithBodyOrNoContent(eroeffnungsuhrzeitModel.map(EroeffnungsuhrzeitDTOMapper::toDTO));
    }

    @Operation(description = "Aktualisiert die Eroeffnungsuhrzeit des Wahllokals {wahlbezirkID}")
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postEroeffnungsuhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final EroeffnungsUhrzeitWriteDTO eroeffnungsuhrzeitWriteDTO) {
        val eroeffnungsuhrzeitToSet = EroeffnungsuhrzeitDTOMapper.toModel(wahlbezirkID, eroeffnungsuhrzeitWriteDTO);
        eroeffnungsuhrzeitService.setEroeffnungsUhrzeit(eroeffnungsuhrzeitToSet);
    }

}
