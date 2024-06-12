package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitService;
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
@RequestMapping("/businessActions/fortsetzungsUhrzeit")
@RequiredArgsConstructor
public class FortsetzungsUhrzeitController extends AbstractController {

    private final FortsetzungsUhrzeitService fortsetzungsUhrzeitService;
    private final FortsetzungsUhrzeitDTOMapper fortsetzungsUhrzeitDTOMapper;

    @Operation(description = "Laden der Fortsetzungsuhrzeit des Urnenwahllokals {wahlbezirkID}")
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<FortsetzungsUhrzeitDTO> getFortsetzungsUhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val fortsetzungsUhrzeitModel = fortsetzungsUhrzeitService.getFortsetzungsUhrzeit(wahlbezirkID);

        return withBodyOrNoContent(fortsetzungsUhrzeitModel.map(fortsetzungsUhrzeitDTOMapper::toDTO));
    }

    @Operation(description = "Aktualisiert die FortsetzungsUhrzeit des Urnenwahllokals {wahlbezirkID}")
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postFortsetzungsUhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID,
        @RequestBody final FortsetzungsUhrzeitWriteDTO fortsetzungsUhrzeitWriteDTO) {
        val fortsetzungsUhrzeitToSet = fortsetzungsUhrzeitDTOMapper.toModel(wahlbezirkID, fortsetzungsUhrzeitWriteDTO);
        fortsetzungsUhrzeitService.setFortsetzungsUhrzeit(fortsetzungsUhrzeitToSet);
    }

}
