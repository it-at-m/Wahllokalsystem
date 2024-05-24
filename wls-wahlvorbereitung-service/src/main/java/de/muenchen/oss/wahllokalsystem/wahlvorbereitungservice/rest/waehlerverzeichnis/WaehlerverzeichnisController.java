package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
@RequestMapping("/businessActions/waehlerverzeichnis")
@RequiredArgsConstructor
public class WaehlerverzeichnisController extends AbstractController {

    private final WaehlerverzeichnisDTOMapper waehlerverzeichnisDTOMapper;

    private final WaehlerverzeichnisService waehlerverzeichnisService;

    @PostMapping("{wahlbezirkID}/{wvzNummer}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postWaehlerverzeichnis(@PathVariable("wahlbezirkID") final String wahlbezirkID, @PathVariable("wvzNummer") final long wvzNummer,
            @RequestBody final WaehlerverzeichnisWriteDTO requestBody) {
        val modelToSet = waehlerverzeichnisDTOMapper.toModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, wvzNummer), requestBody);
        waehlerverzeichnisService.setWaehlerverzeichnis(modelToSet);
    }

    @GetMapping("{wahlbezirkID}/{wvzNummer}")
    public ResponseEntity<WaehlerverzeichnisDTO> getWaehlerverzeichnis(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @PathVariable("wvzNummer") final long wvzNummer) {
        val waehlerverzeichnisModel = waehlerverzeichnisService.getWaehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, wvzNummer));
        return withBodyOrNoContent(waehlerverzeichnisModel.map(waehlerverzeichnisDTOMapper::toDto));
    }

}
