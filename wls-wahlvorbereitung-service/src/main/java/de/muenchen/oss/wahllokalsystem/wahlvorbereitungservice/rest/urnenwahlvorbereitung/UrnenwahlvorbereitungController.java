package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/businessActions/urnenwahlVorbereitung")
@RequiredArgsConstructor
public class UrnenwahlvorbereitungController {

    private final UrnenwahlvorbereitungService service;

    private final UrnenwahlvorbereitungDTOMapper urnenwahlvorbereitungDTOMapper;
    private final UrnenwahlvorbereitungService urnenwahlvorbereitungService;

    @Operation(description = "Laden der Wahlvorbereitungsdaten des Urnenwahllokals {wahlbezirkID}")
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<UrnenwahlvorbereitungDTO> getUrnenwahlVorbereitung(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val urnenwahlvorbereitungModel = service.getUrnenwahlvorbereitung(wahlbezirkID);

        return withBodyOrNoContent(urnenwahlvorbereitungModel.map(urnenwahlvorbereitungDTOMapper::toDTO));
    }

    @Operation(description = "Aktualisiert die Wahlvorbereitungsdaten des Urnenwahllokals {wahlbezirkID}")
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postUrnenwahlvorbereitung(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final UrnenwahlvorbereitungWriteDTO urnenwahlvorbereitungDTO) {
        val vorbereitungToSet = urnenwahlvorbereitungDTOMapper.toModel(wahlbezirkID, urnenwahlvorbereitungDTO);
        urnenwahlvorbereitungService.setUrnenwahlvorbereitung(vorbereitungToSet);
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> body) {
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
