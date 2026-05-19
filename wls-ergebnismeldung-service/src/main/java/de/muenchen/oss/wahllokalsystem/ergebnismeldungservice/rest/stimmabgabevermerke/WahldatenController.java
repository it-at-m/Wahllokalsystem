package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenService;
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
public class WahldatenController extends AbstractController {

  private final WahldatenService wahldatenService;
  private final StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

  @GetMapping(
      "/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/wvz/{waehlerverzeichnisNummer}/stimmabgabevermerke")
  public ResponseEntity<WahldatenDTO> getWahldaten(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer) {
    return okWithBodyOrNoContent(
        wahldatenService
            .getWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer)
            .map(stimmabgabevermerkeDTOMapper::toWahldatenDTO));
  }

  @PostMapping(
      "/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/wvz/{waehlerverzeichnisNummer}/stimmabgabevermerke")
  @ResponseStatus(HttpStatus.CREATED)
  public void setWahldaten(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer,
      @RequestBody WahldatenDTO wahldatenDTO) {
    val wahldatenModel =
        stimmabgabevermerkeDTOMapper.toWahldatenModel(
            wahlID, wahlbezirkID, waehlerverzeichnisNummer, wahldatenDTO);
    wahldatenService.setWahldaten(wahldatenModel);
  }
}
