package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * This controller is responsible for mapping businessActions to REST-endpoints.
 */
@RestController
@RequestMapping(value = "/businessActions")
public class EreignisController {

    private final EreignisDTOMapper ereignisDTOMapper;

    EreignisService ereignisService;

    public EreignisController(EreignisDTOMapper ereignisDTOMapper) {
        this.ereignisDTOMapper = ereignisDTOMapper;
    }

    /**
     * This BusinessAction's purpose is: Laden der besonderen Ereignisse
     * It returns one Ereignis.
     */
    @GetMapping("/ereignisse/{wahlbezirkID}")
    public ResponseEntity<EreignisDTO> getEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        val ereignisFromService = ereignisService.getEreignis(wahlbezirkID);
        return withBodyOrNoContent(ereignisFromService.map(ereignisDTOMapper::toDTO));
    }

    /**
     * This BusinessAction's purpose is: Speichern von besonderen Ereignissen
     */
    @PostMapping("/ereignisse/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public void postEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @RequestBody EreignisWriteDTO ereignisBody) {
        ereignisService.postEreignis(ereignisDTOMapper.toModel(wahlbezirkID, ereignisBody));
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
