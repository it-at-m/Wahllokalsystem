package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractRestController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
@RequestMapping("/businessActions/status")
@RequiredArgsConstructor
public class StatusController extends AbstractRestController {

    private final StatusService statusService;
    private final StatusDTOMapper statusDTOMapper;

    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<StatusDTO> getStatus(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val status = statusService.getStatus(new BezirkUndWahlID(wahlID, wahlbezirkID));
        return okWithBodyOrNoContent(status.map(statusDTOMapper::toDTO));
    }

    @PostMapping("{wahlID}/{wahlbezirkID}")
    public void setStatus(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final StatusDTO statusDTO) {
        statusService.setStatus(new BezirkUndWahlID(wahlID, wahlbezirkID), statusDTOMapper.toModel(statusDTO));
    }

}
