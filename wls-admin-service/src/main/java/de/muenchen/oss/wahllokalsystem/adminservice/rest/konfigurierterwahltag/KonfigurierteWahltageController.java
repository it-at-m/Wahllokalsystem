package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement.KonfigurierterWahltagClientMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class KonfigurierteWahltageController {

    private final KonfigurierteWahltageService konfigurierteWahltageService;

    private final KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Operation(
            description = "Liefert alle konfigurierten Wahltage.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Die konfigurierten Wahltage wurden erfolgreich geliefert."
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Problem beim Verarbeiten der Anfrage.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @GetMapping("konfigurierteWahltage")
    @ResponseStatus(HttpStatus.OK)
    public List<KonfigurierterWahltagDTO> getKonfigurierteWahltage() {
        return konfigurierteWahltageService.getKonfigurierteWahltage().stream().map(konfigurierterWahltagClientMapper::toDto).toList();
    }
}
