package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeListeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlagDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vorschlaege")
public class WahlvorschlagController {

    @GetMapping("referendum/{wahlID}/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public ReferendumvorlagenDTO loadReferendumvorlagen(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GetMapping("wahl/{wahlID}/{wahlbezirkID}")
    @ResponseStatus(HttpStatus.OK)
    public WahlvorschlaegeDTO loadWahlvorschlaege(@PathVariable("wahlID") String wahlID, @PathVariable("wahlbezirkID") String wahlbezirkID) {
        //        throw new UnsupportedOperationException("Not supported yet.");
        //TODO add dummy impl in Service instead
        return new WahlvorschlaegeDTO(wahlbezirkID, wahlID, wahlbezirkID + wahlID,
                Set.of(new WahlvorschlagDTO(UUID.randomUUID().toString(), 1L, "kurzname1", true,
                                Set.of(new KandidatDTO(UUID.randomUUID().toString(), "name1", 1L, true, 1L, true),
                                        new KandidatDTO(UUID.randomUUID().toString(), "name2", 2L, false, 2L, false))),
                        new WahlvorschlagDTO(UUID.randomUUID().toString(), 2L, "kurzname2", true,
                                Set.of(new KandidatDTO(UUID.randomUUID().toString(), "name3", 1L, true, 1L, true),
                                        new KandidatDTO(UUID.randomUUID().toString(), "name4", 2L, false, 2L, false)))));
    }

    @GetMapping("wahl/{wahlID}/liste")
    @ResponseStatus(HttpStatus.OK)
    public WahlvorschlaegeListeDTO loadWahlvorschlaegeListe(@RequestParam("forDate") LocalDate wahltag, @PathVariable("wahlID") String wahlID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
