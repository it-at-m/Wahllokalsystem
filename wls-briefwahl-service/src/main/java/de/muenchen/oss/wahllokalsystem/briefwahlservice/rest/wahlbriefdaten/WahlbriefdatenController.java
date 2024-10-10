package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenService;
import java.util.Optional;
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
@RequestMapping("/businessActions/wahlbriefdaten")
@RequiredArgsConstructor
public class WahlbriefdatenController {

    private final WahlbriefdatenService wahlbriefdatenService;

    private final WahlbriefdatenDTOMapper wahlbriefdatenDTOMapper;

    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<WahlbriefdatenDTO> getWahlbriefdaten(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val wahlbriefdatenFromService = wahlbriefdatenService.getWahlbriefdaten(wahlbezirkID);

        return okWithBodyOrNoContent(wahlbriefdatenFromService.map(wahlbriefdatenDTOMapper::toDTO));
    }

    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public void postWahlbriefdaten(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @RequestBody WahlbriefdatenWriteDTO wahlbriefdaten) {
        wahlbriefdatenService.setWahlbriefdaten(wahlbriefdatenDTOMapper.toModel(wahlbezirkID, wahlbriefdaten));
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
