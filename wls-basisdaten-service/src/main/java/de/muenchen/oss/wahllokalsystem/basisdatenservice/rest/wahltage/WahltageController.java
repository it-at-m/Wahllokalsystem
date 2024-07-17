package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/wahltage")
@RequiredArgsConstructor
@Slf4j
public class WahltageController {

    private final WahltageService wahltageService;
    private final WahltageDTOMapper wahltageDTOMapper;

    @Operation(description = "Laden der Liste der Wahltage, aufsteigend sortiert nach Wahltag-Datum und nicht länger als 3 Monate in der Vergangenheit.")
    @GetMapping
    public ResponseEntity<List<WahltagDTO>> getWahltage() {
        return new ResponseEntity<>(
                wahltageDTOMapper.fromListOfWahltagModelToListOfWahltagDTO(wahltageService.getWahltage()),
                HttpStatus.OK);
    }
}
