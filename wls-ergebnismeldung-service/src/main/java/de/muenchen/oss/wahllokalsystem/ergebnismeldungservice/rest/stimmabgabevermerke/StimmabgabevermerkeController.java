package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
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
@RequestMapping
@RequiredArgsConstructor
public class StimmabgabevermerkeController extends AbstractController {

  private final StimmabgabevermerkeService stimmabgabevermerkeService;
  private final StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

  @GetMapping(
      "/businessActions/stimmabgabevermerke/{wahlbezirkID}/{wahlID}/{waehlerverzeichnisNummer}")
  public ResponseEntity<StimmabgabevermerkeDTO> getWahldaten(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer) {
    return okWithBodyOrNoContent(
        stimmabgabevermerkeService
            .getWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer)
            .map(stimmabgabevermerkeDTOMapper::toWahldatenDTO));
  }

  @PostMapping(
      "/businessActions/stimmabgabevermerke/{wahlbezirkID}/{wahlID}/{waehlerverzeichnisNummer}")
  @ResponseStatus(HttpStatus.OK)
  public void setWahldaten(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer,
      @RequestBody StimmabgabevermerkeDTO stimmabgabevermerkeDTO) {
    val wahldatenModel =
        stimmabgabevermerkeDTOMapper.toWahldatenModel(
            wahlID, wahlbezirkID, waehlerverzeichnisNummer, stimmabgabevermerkeDTO);
    stimmabgabevermerkeService.setWahldaten(wahldatenModel);
  }
}
