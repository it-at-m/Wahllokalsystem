package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.AbstractRestController;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/status")
@RequiredArgsConstructor
public class StatusController extends AbstractRestController {

    private final StatusService statusService;
    private final StatusDTOMapper statusDTOMapper;

    @Operation(description = "Lesen des Bearbeitungsstatus bei der Ergebnisermittelung eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Es existiert ein Zustand",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "204", description = "Es existiert kein Zustand entsprechend der Kriterien"
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @GetMapping("{wahlID}/{wahlbezirkID}")
    public ResponseEntity<StatusDTO> getStatus(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val status = statusService.getStatus(new BezirkUndWahlID(wahlID, wahlbezirkID));
        return okWithBodyOrNoContent(status.map(statusDTOMapper::toDTO));
    }

    @Operation(description = "Setzen des Bearbeitungsstatus bei der Ergebnisermittelung eines Wahlbezirkes für eine Wahl")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Zustand erfolgreich gespeichert",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StatusDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validierung der Anfrage war nicht erfolgreich",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Probleme bei der Verarbeitung der Anfrage",
                            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class)) }
                    )
            }
    )
    @PostMapping("{wahlID}/{wahlbezirkID}")
    public void setStatus(@PathVariable("wahlID") final String wahlID, @PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final StatusDTO statusDTO) {
        statusService.setStatus(new BezirkUndWahlID(wahlID, wahlbezirkID), statusDTOMapper.toModel(statusDTO));
    }

}
