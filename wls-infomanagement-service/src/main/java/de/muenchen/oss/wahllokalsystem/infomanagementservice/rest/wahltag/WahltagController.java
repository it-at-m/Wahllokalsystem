package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
public class WahltagController {

    private final String KONFIGURIERTER_WAHLTAG_PATH = "/konfigurierterWahltag";
    private final String KONFIGURIERTER_WAHLTAG_PATH_DELETE = "/konfigurierterWahltag/{wahltagID}";
    private final String KONFIGURIERTE_WAHLTAGE_PATH = "/konfigurierteWahltage";
    private final String LOGIN_CHECK_PATH = "/loginCheck";

    private final KonfigurierterWahltagService konfigurierterWahltagService;
    private final KonfigurierterWahltagMapper mapper;

    @GetMapping(value = KONFIGURIERTER_WAHLTAG_PATH)
    public ResponseEntity<KonfigurierterWahltagDTO> getKonfigurierterWahltag(@RequestHeader Map<String, Object> headers) {
        val konfigurierterWahltagModel = konfigurierterWahltagService.getKonfigurierterWahltag(headers);
        val konfigurierterWahltagDTO = mapper.toDTO(konfigurierterWahltagModel);
        return withBodyOrNoContent(konfigurierterWahltagDTO);
    }

    //    @GetMapping("{wahlbezirkID}/{waehlerverzeichnisNummer}")
    //    public ResponseEntity<BeanstandeteWahlbriefeDTO> getBeanstandeteWahlbriefe(@PathVariable("wahlbezirkID") String wahlbezirkID,
    //        @PathVariable("waehlerverzeichnisNummer") Long waehlerverzeichnisNummer) {
    //        val referenceModel = beanstandeteWahlbriefeDTOMapper.toReferenceModel(wahlbezirkID, waehlerverzeichnisNummer);
    //        val beanstandeteWahlbriefeFromService = beanstandeteWahlbriefeDTOMapper.toDTO(beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(referenceModel));
    //        return withBodyOrNoContent(beanstandeteWahlbriefeFromService);
    //    }

    //    /**
    //     * This BusinessAction's purpose is: Aktualisiert den konfigurierten Wahltag
    //     */
    //    @RequestMapping(value = KONFIGURIERTER_WAHLTAG_PATH, method = RequestMethod.POST)
    //    public ResponseEntity<?> postKonfigurierterWahltag(@RequestHeader Map<String, Object> headers, @RequestBody KonfigurierterWahltag_ konfigurierterWahltag) {
    //        postKonfigurierterWahltagactionService.postKonfigurierterWahltag(headers, konfigurierterWahltag);
    //        return new ResponseEntity<>(HttpStatus.OK);
    //    }
    //
    //    /**
    //     * This BusinessAction's purpose is: Loescht den konfigurierten Wahltag
    //     */
    //    @RequestMapping(value = KONFIGURIERTER_WAHLTAG_PATH_DELETE, method = RequestMethod.DELETE)
    //    public ResponseEntity<?> deleteKonfigurierterWahltag(@RequestHeader Map<String, Object> headers, @PathVariable("wahltagID") String wahltagID) {
    //        deleteKonfigurierterWahltagactionService.deleteKonfigurierterWahltag(headers, wahltagID);
    //        return new ResponseEntity<>(HttpStatus.OK);
    //    }
    //
    //    /**
    //     * This BusinessAction's purpose is: Liefert alle konfigurierten Wahltage It returns multiple KonfigurierterWahltag_.
    //     */
    //    @RequestMapping(value = KONFIGURIERTE_WAHLTAGE_PATH, method = RequestMethod.GET)
    //    public ResponseEntity<List<KonfigurierterWahltag_>> getKonfigurierteWahltage(@RequestHeader Map<String, Object> headers) {
    //        List<KonfigurierterWahltag_> konfigurierteWahltage = getKonfigurierteWahltageactionService.getKonfigurierteWahltage(headers);
    //        if (konfigurierteWahltage == null || konfigurierteWahltage.isEmpty()) {
    //            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //        }
    //        return new ResponseEntity<>(konfigurierteWahltage, HttpStatus.OK);
    //    }
    //
    //    @RequestMapping(value = LOGIN_CHECK_PATH + "/{wahltagID}", method = RequestMethod.GET)
    //    public ResponseEntity<Boolean> getLoginCheck(@RequestHeader Map<String, Object> headers, @PathVariable("wahltagID") String wahltagID) {
    //        boolean result = getLoginCheckactionService.getLoginCheck(headers, wahltagID);
    //        return new ResponseEntity<>(result, HttpStatus.OK);
    //    }

    private <T> ResponseEntity<T> withBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
