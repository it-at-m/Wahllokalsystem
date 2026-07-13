package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/businessActions/stimmmzettelerfassungsWorkflow")
@RequiredArgsConstructor
public class StimmzettelerfassungController extends AbstractController {

  private final StimmzettelerfassungService stimmzettelerfassungService;

  private final ErfassungStatusDTOMapper erfassungStatusDTOMapper;

  @Operation(description = "Erfassen des Workflow-Status für die digitalen Stimmzettelerfassung")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "201", description = "Der Status wurde erfasst")})
  @PostMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveStimmzettelerfassungStatus(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @RequestBody final StimmzettelerfassungStatusDTO stimmzettelerfassungStatusDTO) {

    stimmzettelerfassungService.saveStimmzettelerfassungStatus(
        new BezirkUndWahlID(wahlID, wahlbezirkID),
        erfassungStatusDTOMapper.toModel(stimmzettelerfassungStatusDTO.status()));
  }

  @Operation(description = "Lesen des Workflow-Status für die digitalen Stimmzettelerfassung")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es wurde ein Status gepflegt. Dieser wird zurückgegeben.",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = ErfassungStatusDTO.class)))
            }),
        @ApiResponse(
            responseCode = "204",
            description = "Es wurden kein Status gepflegt",
            content = {@Content()})
      })
  @GetMapping("wahl/{wahlID}/wahlbezirk/{wahlbezirkID}")
  public ResponseEntity<ErfassungStatusDTO> getStimmzettelerfassungStatus(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID) {
    return okWithBodyOrNoContent(
        stimmzettelerfassungService
            .getStimmzettelerfassungStatus(new BezirkUndWahlID(wahlID, wahlbezirkID))
            .map(erfassungStatusDTOMapper::toDTO));
  }
}
