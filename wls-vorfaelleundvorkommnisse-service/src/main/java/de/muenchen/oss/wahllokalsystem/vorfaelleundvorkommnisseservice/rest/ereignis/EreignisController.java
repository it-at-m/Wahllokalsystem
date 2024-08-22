package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import lombok.val;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * This controller is responsible for mapping businessActions to REST-endpoints.
 */
@RestController // todo: brauche ich diese annotation ? von wahlbriefdatencontroller
@BasePathAwareController    // todo: was macht diese annotation ?
// todo: von den zwei oberen annotations nur eine denke ich
@RequestMapping(value = "/businessActions") // todo: kommt hier das "ereignisse" aus den unteren paths rein?
public class EreignisController {

    private final String VORFAELLEUNDVORKOMMNISSE_PATH = "/ereignisse/{wahlbezirkID}";  // todo: löschen oder an Get/Post Mapping übergeben?
    private final EreignisDTOMapper ereignisDTOMapper;

    EreignisService ereignisService;

    public EreignisController(EreignisDTOMapper ereignisDTOMapper) {
        this.ereignisDTOMapper = ereignisDTOMapper;
    }

    /**
     * This BusinessAction's purpose is: Laden der besonderen Ereignisse
     * It returns one Ereignis.
     */
    @GetMapping("/ereignisse/{wahlbezirkID}")    // todo: stimmt der pfad?
    public ResponseEntity<EreignisDTO> getEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        val ereignisFromService = ereignisService.getEreignis(wahlbezirkID);
        return withBodyOrNoContent(ereignisFromService.map(ereignisDTOMapper::toDTO));
    }

    /**
     * This BusinessAction's purpose is: Speichern von besonderen Ereignissen
     */
    @PostMapping("/ereignisse/{wahlbezirkID}")   // todo: stimmt der pfad?
    @ResponseStatus(HttpStatus.OK)
    public void postEreignis(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @RequestBody EreignisWriteDTO ereignisBody) {
        ereignisService.postEreignis(ereignisDTOMapper.toModel(wahlbezirkID, ereignisBody));
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> optionalBody) {
        return optionalBody.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
