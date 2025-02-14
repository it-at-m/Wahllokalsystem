package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahlenController {

    private final WahlenService wahlenService;

    private final WahlDTOMapper wahlenDTOMapper;

    @GetMapping("/wahlen/{wahltagID}")
    public ResponseEntity<List<WahlDTO>> getWahlen(@PathVariable("wahltagID") final String wahltagID) {
        final List<WahlDTO> result = wahlenDTOMapper.toDtoList(wahlenService.getWahlen(wahltagID));
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/wahlen/{wahltagID}")
    public ResponseEntity<?> updateWahlen(@RequestBody final List<WahlDTO> wahlen,
            @PathVariable("wahltagID") final String wahltagID) {
        wahlenService.updateWahlen(wahlenDTOMapper.toModelList(wahlen), wahltagID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
