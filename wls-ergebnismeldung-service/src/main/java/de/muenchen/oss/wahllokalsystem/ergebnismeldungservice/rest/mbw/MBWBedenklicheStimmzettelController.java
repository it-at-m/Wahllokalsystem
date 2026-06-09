package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.List;
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
@RequestMapping("/mbw/wahl")
@RequiredArgsConstructor
public class MBWBedenklicheStimmzettelController extends AbstractController {

  private final MBWBedenklicheStimmzettelService bedenklicheStimmzettelService;
  private final BedenklicherStimmzettelDTOMapper bedenklicheStimmzettelDTOMapper;

  @GetMapping("/{wahlID}/wahlbezirk/{wahlbezirkID}/bedenklicheStimmzettel")
  public ResponseEntity<Collection<BedenklicherStimmzettelDTO>>
      getBedenklicheStimmzettelByOrderIndexAsc(
          @PathVariable("wahlID") final String wahlID,
          @PathVariable("wahlbezirkID") final String wahlbezirkID) {
    val optionalOfbedenklicherStimmzettel =
        bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
            new BezirkUndWahlID(wahlID, wahlbezirkID));

    return okWithBodyOrNoContent(
        optionalOfbedenklicherStimmzettel.map(bedenklicheStimmzettelDTOMapper::toDTO));
  }

  @PostMapping("/{wahlID}/wahlbezirk/{wahlbezirkID}/bedenklicheStimmzettel")
  @ResponseStatus(HttpStatus.CREATED)
  public void setBedenklicheStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @RequestBody List<BedenklicherStimmzettelDTO> requestBody) {
    val modelToSave = requestBody.stream().map(bedenklicheStimmzettelDTOMapper::toModel).toList();
    bedenklicheStimmzettelService.setBedenklicheStimmzettel(
        new BezirkUndWahlID(wahlID, wahlbezirkID), modelToSave);
  }
}
