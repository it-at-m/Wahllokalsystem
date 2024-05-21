package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/urnenwahlVorbereitung")
@RequiredArgsConstructor
public class UrnenwahlvorbereitungController {

    private final UrnenwahlvorbereitungService service;

    private final UrnenwahlvorbereitungDTOMapper urnenwahlvorbereitungDTOMapper;

    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<UrnenwahlvorbereitungDTO> getUrnenwahlVorbereitung(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val urnenwahlvorbereitungModel = service.getUrnenwahlvorbereitung(wahlbezirkID);

        return withBodyOrNoContent(urnenwahlvorbereitungModel.map(urnenwahlvorbereitungDTOMapper::toDTO));
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> body) {
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
