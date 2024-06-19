package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/businessActions/briefwahlvorbereitung")
@RequiredArgsConstructor
public class BriefwahlvorbereitungController extends AbstractController {

    private final BriefwahlvorbereitungService briefwahlvorbereitungService;
    private final BriefwahlvorbereitungDTOMapper briefwahlvorbereitungDTOMapper;

    @Operation(description = "Wahlvorbereitungsdaten des Briefwahllokals {wahlbezirkID}")
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<BriefwahlvorbereitungDTO> getBriefwahlvorbereitung(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val briefwahlvorbereitungModel = briefwahlvorbereitungService.getBriefwahlvorbereitung(wahlbezirkID);

        return withBodyOrNoContent(briefwahlvorbereitungModel.map(briefwahlvorbereitungDTOMapper::toDTO));
    }

    @Operation(description = "Aktualisiert die Wahlvorbereitungsdaten des Briefwahllokals {wahlbezirkID}")
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postBriefwahlvorbereitung(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final BriefwahlvorbereitungWriteDTO briefwahlvorbereitungWriteDTO) {
        val briefwahlvorbereitungToSet = briefwahlvorbereitungDTOMapper.toModel(wahlbezirkID, briefwahlvorbereitungWriteDTO);
        briefwahlvorbereitungService.setBriefwahlvorbereitung(briefwahlvorbereitungToSet);
    }

}
