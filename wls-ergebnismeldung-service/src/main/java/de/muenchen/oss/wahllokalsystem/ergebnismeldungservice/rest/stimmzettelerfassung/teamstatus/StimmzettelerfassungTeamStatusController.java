package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.TeamStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/stimmzettelerfassung")
@RequiredArgsConstructor
public class StimmzettelerfassungTeamStatusController extends AbstractController {

  private final TeamStatusService teamStatusService;
  private final ErfassungTeamStatusDTOMapper erfassungTeamStatusDTOMapper;

  @Operation(description = "Erfassen des Team-Status für die digitale Stimmzettelerfassung")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "201", description = "Der Status wurde erfasst")})
  @PostMapping("/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/status")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveStimmzettelerfassungTeamStatus(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("teamID") final String teamID,
      @RequestBody final StimmzettelerfassungTeamStatusDTO erfassungTeamStatusDTO) {

    teamStatusService.saveTeamStatus(
        new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID),
        erfassungTeamStatusDTOMapper.toModel(erfassungTeamStatusDTO.status()));
  }

  @Operation(description = "Lesen des Team-Status für die digitale Stimmzettelerfassung")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Es wurde ein Status gepflegt. Dieser wird zurückgegeben."),
        @ApiResponse(
            responseCode = "204",
            description = "Es wurden kein Status gepflegt",
            content = {@Content()})
      })
  @GetMapping("/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/status")
  public ResponseEntity<StimmzettelerfassungTeamStatusDTO> getStimmzettelerfassungTeamStatus(
      @PathVariable("wahlID") final String wahlID,
      @PathVariable("wahlbezirkID") final String wahlbezirkID,
      @PathVariable("teamID") final String teamID) {

    return okWithBodyOrNoContent(
        teamStatusService
            .getTeamStatus(new TeamBezirkUndWahlIDModel(teamID, wahlbezirkID, wahlID))
            .map(erfassungTeamStatusDTOMapper::toDTO)
            .map(StimmzettelerfassungTeamStatusDTO::new));
  }
}
