package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitService;
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
@RequestMapping("/businessActions/unterbrechungsUhrzeit")
@RequiredArgsConstructor
public class UnterbrechungsUhrzeitController extends AbstractController {

    private final UnterbrechungsUhrzeitService unterbrechungsuhrzeitService;
    private final UnterbrechungsUhrzeitDTOMapper UnterbrechungsuhrzeitDTOMapper;

    @Operation(description = "Laden der Unterbrechungsuhrzeit des Urnenwahllokals {wahlbezirkID}")
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<UnterbrechungsUhrzeitDTO> getUnterbrechungsuhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val unterbrechungsuhrzeitModel = unterbrechungsuhrzeitService.getUnterbrechungsUhrzeit(wahlbezirkID);

        return withBodyOrNoContent(unterbrechungsuhrzeitModel.map(UnterbrechungsuhrzeitDTOMapper::toDTO));
    }

    @Operation(description = "Aktualisiert die Unterbrechungsuhrzeit des Urnenwahllokals {wahlbezirkID}")
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postUnterbrechungsuhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final UnterbrechungsUhrzeitWriteDTO unterbrechungsuhrzeitWriteDTO) {
        val unterbrechungsuhrzeitToSet = UnterbrechungsuhrzeitDTOMapper.toModel(wahlbezirkID, unterbrechungsuhrzeitWriteDTO);
        unterbrechungsuhrzeitService.setUnterbrechungsUhrzeit(unterbrechungsuhrzeitToSet);
    }

}
