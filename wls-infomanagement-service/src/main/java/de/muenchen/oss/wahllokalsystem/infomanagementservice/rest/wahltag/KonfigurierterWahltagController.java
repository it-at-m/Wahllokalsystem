package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
public class KonfigurierterWahltagController {

    private final String KONFIGURIERTER_WAHLTAG_PATH = "/konfigurierterWahltag";
    private final String KONFIGURIERTER_WAHLTAG_PATH_DELETE = "/konfigurierterWahltag/{wahltagID}";
    private final String KONFIGURIERTE_WAHLTAGE_PATH = "/konfigurierteWahltage";
    private final String LOGIN_CHECK_PATH = "/loginCheck";

    private final KonfigurierterWahltagService konfigurierterWahltagService;
    private final KonfigurierterWahltagDTOMapper mapper;

    @GetMapping(value = KONFIGURIERTER_WAHLTAG_PATH)
    public ResponseEntity<KonfigurierterWahltagDTO> getKonfigurierterWahltag() {
        val konfigurierterWahltagDTO = mapper.toDTO(konfigurierterWahltagService.getKonfigurierterWahltag());
        return okWithBodyOrNoContent(konfigurierterWahltagDTO);
    }

    @PostMapping(value = KONFIGURIERTER_WAHLTAG_PATH)
    public void setKonfigurierterWahltag(@RequestBody KonfigurierterWahltagDTO konfigurierterWahltagDTO) {
        konfigurierterWahltagService.setKonfigurierterWahltag(mapper.toModel(konfigurierterWahltagDTO));
    }

    @DeleteMapping(value = KONFIGURIERTER_WAHLTAG_PATH_DELETE)
    public void deleteKonfigurierterWahltag(@PathVariable("wahltagID") String wahltagID) {
        konfigurierterWahltagService.deleteKonfigurierterWahltag(wahltagID);
    }

    @GetMapping(value = KONFIGURIERTE_WAHLTAGE_PATH)
    public ResponseEntity<List<KonfigurierterWahltagDTO>> getKonfigurierteWahltage() {
        List<KonfigurierterWahltagDTO> konfigurierteWahltageDTO = mapper.toDTOList(konfigurierterWahltagService.getKonfigurierteWahltage());
        return okWithBodyOrNoContent(konfigurierteWahltageDTO);
    }

    @GetMapping(value = LOGIN_CHECK_PATH + "/{wahltagID}")
    public ResponseEntity<Boolean> isWahltagActive(@PathVariable("wahltagID") String wahltagID) {
        boolean result = konfigurierterWahltagService.isWahltagActive(wahltagID);
        return okWithBodyOrNoContent(result);
    }

    private <T> ResponseEntity<T> okWithBodyOrNoContent(final T body) {
        if (body == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(body);
        }
    }
}
