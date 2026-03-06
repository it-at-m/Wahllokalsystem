package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel.StimmzettelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/stimmzettel")
@RequiredArgsConstructor
public class StimmzettelController {

  private final StimmzettelService stimmzettelService;
  private final StimmzettelDTOMapper stimmzettelDTOMapper;

  @GetMapping
  public List<WaehlerStimmzettelDTO> getStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID) {
    return stimmzettelService.getStimmzettel(wahlID, wahlbezirkID).stream()
        .map(stimmzettelDTOMapper::toDTO)
        .toList();
  }

  @PostMapping
  public void postStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @RequestBody final List<WaehlerStimmzettelDTO> stimmzettelToSave) {
    stimmzettelService.saveStimmzettel(
        stimmzettelToSave.stream().map(stimmzettelDTOMapper::toModel).toList());
  }
}
