package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WahllokalBenutzerRestController {

    private final UserDTOMapper userDTOMapper;

    private final UserService wahllokalBenutzerService;

    @Operation(
            description = "Generiert Wahllokalbenutzer zum angegebenen Wahltag. Für jeden Benutzer der angelegt werden soll, muss eine WahllokalUserInfo im Body vorhanden sein. Als Antwort wird eine CSV-Liste mit den generierten Benutzernamen zurückgegeben."
    )
    @PostMapping(value = "/generateAndExportWahllokalbenutzer/{wahltagID}")
    @ResponseBody
    public ResponseEntity<String> createAndExportWahllokalBenutzer(@PathVariable("wahltagID") String wahltagID,
            @RequestBody List<WahllokalUserInfoDTO> wahllokalUserInfo) {
        log.info("Erstelle Benutzer für Wahltag-ID <{}>.", wahltagID);
        try {
            String s = wahllokalBenutzerService.generateWahllokalBenutzer(userDTOMapper.toModel(wahltagID, wahllokalUserInfo));
            return new ResponseEntity<>(s, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("#createAndExportWahllokalBenutzer error: ", e);
            return new ResponseEntity<>("createAndExportWahllokalBenutzer error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description = "Liefert einen CSV-String der alle Wahllokalbenutzernamen zum angegebenen Wahltag enthält.")
    @GetMapping(value = "/exportWahllokalbenutzer/{wahltagID}")
    @ResponseBody
    public ResponseEntity<String> exportWahllokalBenutzer(@PathVariable("wahltagID") String wahltagID) {
        log.info("Exportiere Benutzer für Wahltag-ID <{}>.", wahltagID);
        try {
            String s = wahllokalBenutzerService.exportWahllokalBenutzer(wahltagID);
            return new ResponseEntity<>(s, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("#exportWahllokalBenutzer error: ", e);
            return new ResponseEntity<>("#exportWahllokalBenutzer error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description = "Löscht alle Wahllokalbenutzer zum angegebenen Wahltag unwiederruflich.")
    @DeleteMapping(value = "/deleteWahllokalbenutzer/{wahltagID}")
    public void deleteWahllokalBenutzer(@PathVariable("wahltagID") String wahltagID) {
        log.info("Lösche Benutzer für Wahltag-ID <{}>.", wahltagID);
        wahllokalBenutzerService.deleteWahllokalBenutzer(wahltagID);
    }
}
