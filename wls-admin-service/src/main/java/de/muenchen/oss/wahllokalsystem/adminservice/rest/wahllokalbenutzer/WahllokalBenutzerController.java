package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/")
@RequiredArgsConstructor
@Slf4j
public class WahllokalBenutzerController {

    private final WahllokalBenutzerService wahllokalBenutzerService;
    private final CsvFileDTOMapper csvFileDTOMapper;

    @Operation(
            description = "Generiert die Benutzer fuer den gegeben Wahltag.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Benutzer erfolgreich generiert.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CsvFileDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Benutzer können nicht generiert werden."),
                    @ApiResponse(
                            responseCode = "165", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("generateWahllokalbenutzer/{wahltagID}")
    public CsvFileDTO generateWahllokalbenutzer(@PathVariable("wahltagID") final String wahltagID) {
        return csvFileDTOMapper.toDTO(wahllokalBenutzerService.generateWahllokalbenutzer(wahltagID));
    }

    @Operation(description = "Export der WahllokalBenutzer für WahltagID { wahltagID }.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Benutzer erfolgreich exportiert.",
                            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CsvFileDTO.class))) }
                    ),
                    @ApiResponse(responseCode = "400", description = "Benutzer können nicht generiert werden."),
                    @ApiResponse(
                            responseCode = "165", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("exportWahllokalBenutzer/{wahltagID}")
    public CsvFileDTO exportWahllokalBenutzer(@PathVariable("wahltagID") final String wahltagID) {
        return csvFileDTOMapper.toDTO(wahllokalBenutzerService.exportWahllokalBenutzer(wahltagID));
    }

    @Operation(description = "Löscht alle Benutzerdaten aller Wahllokale fuer eine WahlId mithilfe einer wahltagID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Benutzer erfolgreich gelöscht."),
                    @ApiResponse(responseCode = "400", description = "Benutzer können wegen Client-Kommunikationfehler nicht gelöscht werden."),
                    @ApiResponse(responseCode = "165", description = "Anfrageparameter sind fehlerhaft")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("deleteWahllokalBenutzer/{wahltagID}")
    public void deleteWahllokalBenutzer(@PathVariable("wahltagID") final String wahltagID) {
        wahllokalBenutzerService.deleteWahllokalBenutzer(wahltagID);
    }
}
