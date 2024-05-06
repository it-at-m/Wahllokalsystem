package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/beanstandeteWahlbriefe")
@RequiredArgsConstructor
public class BeanstandeteWahlbriefeController {

    private final BeanstandeteWahlbriefeService beanstandeteWahlbriefeService;

    private final BeanstandeteWahlbriefeDTOMapper beanstandeteWahlbriefeDTOMapper;

    @GetMapping("{wahlbezirkID}/{waehlerverzeichnisNummer}")
    public ResponseEntity<BeanstandeteWahlbriefeDTO> getBeanstandeteWahlbriefeDTO(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") Long waehlerverzeichnisNummer) {
        val referenceModel = beanstandeteWahlbriefeDTOMapper.toReferenceModel(wahlbezirkID, waehlerverzeichnisNummer);
        val beanstandeteWahlbriefeFromService = beanstandeteWahlbriefeDTOMapper.toDTO(beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(referenceModel));
        return withBodyOrNoContent(beanstandeteWahlbriefeFromService);
    }

    @PostMapping("{wahlbezirkID}/{waehlerverzeichnisNummer}")
    public void addBeanstandeteWahlbriefe(@PathVariable("wahlbezirkID") String wahlbezirkID,
            @PathVariable("waehlerverzeichnisNummer") Long waehlerverzeichnisNummer,
            @RequestBody BeanstandeteWahlbriefeCreateDTO beanstandeteWahlbriefeCreateDTO) {
        val createModel = beanstandeteWahlbriefeDTOMapper.toCreateModel(beanstandeteWahlbriefeCreateDTO, wahlbezirkID, waehlerverzeichnisNummer);
        beanstandeteWahlbriefeService.addBeanstandeteWahlbriefe(createModel);
    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
