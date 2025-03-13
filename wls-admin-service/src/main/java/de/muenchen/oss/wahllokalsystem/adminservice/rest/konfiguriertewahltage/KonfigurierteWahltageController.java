package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage.KonfigurierteWahltageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class KonfigurierteWahltageController {

    private final KonfigurierteWahltageService konfigurierteWahltageService;

    private final KonfigurierterWahltagDTOMapper konfigurierterWahltagDTOMapper;

    @Operation(
            description = "Liefert alle konfigurierten Wahltage.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die konfigurierten Wahltage wurden erfolgreich geliefert."
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Keine konfigurierten Wahltage vorhanden"
                    )
            }
    )
    @GetMapping("konfigurierteWahltage")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<KonfigurierterWahltagDTO>> getKonfigurierteWahltage() {
        val konfigurierteWahltageDTOList = konfigurierteWahltageService.getKonfigurierteWahltage().stream().map(konfigurierterWahltagDTOMapper::toDTO).toList();
        if (konfigurierteWahltageDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(konfigurierteWahltageDTOList, HttpStatus.OK);
    }

    @Operation(
            description = "Liefert alle konfigurierten Wahltage.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die konfigurierten Wahltage wurden erfolgreich geliefert."
                    ),

            }
    )
    @PostMapping("konfigurierterWahltag")
    @ResponseStatus(HttpStatus.OK)
    public void postKonfigurierterWahltag(@RequestBody KonfigurierterWahltagDTO konfigurierterWahltagDTO) {
        val modelToSave = konfigurierterWahltagDTOMapper.toModel(konfigurierterWahltagDTO);

        konfigurierteWahltageService.postKonfigurierterWahltag(modelToSave);
    }
}
