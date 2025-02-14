package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage.WahltageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahltageController {

    private final WahltageService wahltageService;

    private final WahltagDTOMapper wahltagDTOMapper;

    @GetMapping("/wahltage")
    public ResponseEntity<List<WahltagDTO>> getWahltage() {
        final List<WahltagDTO> result = wahltagDTOMapper.toDtoList(wahltageService.getWahltage());
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

}
