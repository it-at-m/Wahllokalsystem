package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(
      description =
          "Lesen der Stimmabgabevermerke eines Wahlbezirkes für die Wahl eines Wählerverzeichnisses.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es existieren Stimmabgabevermerke.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = StimmabgabevermerkeDTO.class))
            }),
        @ApiResponse(
            responseCode = "204",
            description =
                "Es existieren keine Stimmabgabevermerke entsprechend der Such-Kriterien.",
            content = {@Content()})
      })
  @GetMapping(
      "/businessActions/stimmabgabevermerke/{wahlbezirkID}/{wahlID}/{waehlerverzeichnisNummer}")
  public ResponseEntity<StimmabgabevermerkeDTO> getStimmabgabevermerke(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer) {
    return okWithBodyOrNoContent(
        stimmabgabevermerkeService
            .getStimmabgabevermerke(wahlbezirkID, wahlID, waehlerverzeichnisNummer)
            .map(stimmabgabevermerkeDTOMapper::toStimmabgabevermerkeDTO));
  }

  @Operation(
      description =
          "Speichern der Stimmabgabevermerke eines Wahlbezirkes für die Wahl eines Wählerverzeichnisses.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stimmabgabevermerke erfolgreich gespeichert")
      })
  @PostMapping(
      "/businessActions/stimmabgabevermerke/{wahlbezirkID}/{wahlID}/{waehlerverzeichnisNummer}")
  @ResponseStatus(HttpStatus.OK)
  public void postStimmabgabevermerke(
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("waehlerverzeichnisNummer") final Long waehlerverzeichnisNummer,
      @RequestBody StimmabgabevermerkeDTO stimmabgabevermerkeDTO) {
    val wahldatenModel =
        stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeModel(
            wahlID, wahlbezirkID, waehlerverzeichnisNummer, stimmabgabevermerkeDTO);
    stimmabgabevermerkeService.postStimmabgabevermerke(wahldatenModel);
  }
}
