package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/referendumvorlagen")
@RequiredArgsConstructor
public class ReferendumvorlageController {

    private final ReferendumvorlageService referendumvorlageService;

    private final ReferendumvorlagenDTOMapper referendumvorlagenDTOMapper;

    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ReferendumvorlagenDTO getReferendumvorlagen(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        return referendumvorlagenDTOMapper.toDTO(referendumvorlageService.loadReferendumvorlagen(referendumvorlagenDTOMapper.toModel(wahlbezirkID, wahlID)));
    }
}
