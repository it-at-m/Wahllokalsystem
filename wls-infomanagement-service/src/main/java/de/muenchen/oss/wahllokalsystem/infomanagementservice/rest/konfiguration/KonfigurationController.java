package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenListenDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationSetDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/businessActions")
@RequiredArgsConstructor
public class KonfigurationController {

    private final KonfigurationService konfigurationService;

    private final KonfigurationDTOMapper konfigurationDTOMapper;

    @Operation(
            description = "Gibt die Konfiguration { key } zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Konfiguration erfolgreich zurückgegeben."),
                    @ApiResponse(responseCode = "204", description = "Keine Konfiguration vorhanden."),
            }
    )
    @GetMapping("/konfiguration/{key}")
    public ResponseEntity<KonfigurationDTO> getKonfiguration(@PathVariable("key") KonfigurationKey key) {
        val konfiguration = konfigurationService.getKonfiguration(konfigurationDTOMapper.toModelKey(key));

        return konfiguration.map(konfigurationModel -> ResponseEntity.ok(konfigurationDTOMapper.toDTO(konfigurationModel)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            description = "Speichert die Konfiguration { key } zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Konfiguration erfolgreich gespeichert."),
            }
    )
    @PostMapping("/konfiguration/{key}")
    public void postKonfiguration(@PathVariable("key") final KonfigurationKey key, @RequestBody final KonfigurationSetDTO konfigurationSetDTO) {
        val konfigurationSetModel = konfigurationDTOMapper.toSetModel(key, konfigurationSetDTO);

        konfigurationService.setKonfiguration(konfigurationSetModel);
    }

    @Operation(
            description = "Gibt alle Konfigurationen zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Konfigurationen erfolgreich geladen."),
            }
    )
    @GetMapping("/konfiguration")
    public ResponseEntity<Iterable<KonfigurationDTO>> getKonfigurations() {
        val allKonfigurations = konfigurationService.getAllKonfigurations();

        if (allKonfigurations == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allKonfigurations.stream().map(konfigurationDTOMapper::toDTO).toList());
        }
    }

    @Operation(
            description = "Gibt eine Konfiguration anhand der Loginzeit { key } zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Konfigurationen erfolgreich geladen."),
            }
    )
    @GetMapping("/konfigurationUnauthorized/{key}")
    public ResponseEntity<KonfigurationDTO> getKonfigurationUnauthorized(@PathVariable("key") final KonfigurationKey key) {
        val konfiguration = konfigurationService.getKonfigurationUnauthorized(konfigurationDTOMapper.toModelKey(key));

        return konfiguration.map(konfigurationModel -> ResponseEntity.ok(konfigurationDTOMapper.toDTO(konfigurationModel)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(
            description = "Gibt die Objekt mit einer Liste alle Kennbuchstaben zurück .",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Objekt mit Liste aller Kennbuchstaben erfolgreich geladen."),
            }
    )
    @GetMapping("/kennbuchstaben")
    public KennbuchstabenListenDTO getKennbuchstabenListen() {
        return konfigurationDTOMapper.toDTO(konfigurationService.getKennbuchstabenListen());
    }

}
