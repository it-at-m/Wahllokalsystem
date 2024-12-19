package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungService;
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
@RequestMapping("/businessActions/begruendung")
@RequiredArgsConstructor
public class BegruendungController {

    private final BegruendungService begruendungService;

    private final BegruendungDTOMapper begruendungDTOMapper;

    @GetMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    public ResponseEntity<BegruendungDTO> getBegruendung(@PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("wahlID") final String wahlID,
            @PathVariable("stapelart") final Stapelart stapelart) {
        val referenceModel = begruendungDTOMapper.toReferenceModel(wahlbezirkID, wahlID, stapelart);
        val begruendungFromService = begruendungDTOMapper.toDTO(begruendungService.getBegruendung(referenceModel));

        return okWithBodyOrNoContent(begruendungFromService);
    }

    @PostMapping("{wahlbezirkID}/{wahlID}/{stapelart}")
    @ResponseStatus(HttpStatus.OK)
    public void postBegruendung(@PathVariable("wahlbezirkID") String wahlbezirkID, @PathVariable("wahlID") String wahlID,
            @PathVariable("stapelart") Stapelart stapelart,
            @RequestBody BegruendungDTO begruendungDTO) {
        val modelToSave = begruendungDTOMapper.toModel(begruendungDTO, wahlbezirkID, wahlID, stapelart);
        begruendungService.setBegruendung(modelToSave);
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
