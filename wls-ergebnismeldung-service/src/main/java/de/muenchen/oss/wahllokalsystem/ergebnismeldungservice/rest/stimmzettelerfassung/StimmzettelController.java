package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.StimmzettelModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stimmzettelerfassung")
@RequiredArgsConstructor
public class StimmzettelController {

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
                          contentSchema = StimmzettelModel.class,
                          additionalPropertiesSchema = StimmzettelModel.class))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "Es sind keine Stimmzettel gespeichert",
            content = {@Content()})
      })
  @GetMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/stimmzettel")
  public Map<String, StimmzettelModel> getStimmzettel(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("teamID") final String teamID) {
    return null;
  }
}
