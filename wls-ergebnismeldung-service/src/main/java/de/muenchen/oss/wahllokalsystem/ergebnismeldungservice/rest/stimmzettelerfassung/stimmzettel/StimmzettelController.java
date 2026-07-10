package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOwnerModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/stimmzettelerfassung")
@RequiredArgsConstructor
public class StimmzettelController {

  private final StimmzettelDTOMapper stimmzettelDTOMapper;
  private final StimmzettelService stimmzettelService;

  @Operation(description = "Lesen Stimmzettel eines Team in einem Wahlbezirk einer Wahl")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es sind Stimmzettel gespeichert",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema =
                      @Schema(
                          type = "object",
                          description = "Map: Key = Stimmzettelkennung, Value = StimmzettelModel",
                          additionalProperties =
                              Schema.AdditionalPropertiesValue.USE_ADDITIONAL_PROPERTIES_ANNOTATION,
                          contentSchema = StimmzettelOfTeamModel.class,
                          additionalPropertiesSchema = StimmzettelOfTeamModel.class))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "Es sind keine Stimmzettel gespeichert",
            content = {@Content()})
      })
  @GetMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/stimmzettel")
  public ResponseEntity<List<StimmzettelOfTeamDTO>> getStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("teamID") final String teamID) {
    val stimmzettel =  stimmzettelService
        .getStimmzettel(new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID))
        .stream()
        .map(stimmzettelDTOMapper::toDTO)
        .toList();

    return stimmzettel.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT.value()).build() : ResponseEntity.ok(stimmzettel);
  }

  @PostMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/stimmzettel")
  @ResponseStatus(HttpStatus.CREATED)
  public void postStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("teamID") final String teamID,
      @RequestBody List<StimmzettelOfTeamDTO> stimmzettel) {
    val modelValuesToSave = stimmzettel.stream().map(stimmzettelDTOMapper::toModel).toList();
    stimmzettelService.saveStimmzettel(
        new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID), modelValuesToSave);
  }

  @GetMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/anzahlStimmzettel")
  public int getAnzahlStimmzettel(
          @PathVariable("wahlID") final String wahlID,
          @PathVariable("wahlbezirkID") final String wahlbezirkID) {
    return stimmzettelService.getAnzahlStimmzettel(new BezirkUndWahlID(wahlID, wahlbezirkID));
  }
}
