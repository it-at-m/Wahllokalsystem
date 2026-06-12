package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.nachlieferungsbezirke.NachlieferungsbezirkeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/businessActions/nachlieferungsbezirke")
@RequiredArgsConstructor
@Slf4j
public class NachlieferungsbezirkeController {

  private final NachlieferungsbezirkeService nachlieferungsbezirkeService;

  private final FileMapper fileMapper;

  private final ExceptionFactory exceptionFactory;

  @GetMapping("{wahltagID}/{wahlbezirkID}")
  @Operation(
      description = "Abrufen, ob es sich beim Wahlbezirk um einen Nachlieferungsbezirk handelt",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Überprüfung des Wahlbezirks erfolgreich zurückgegeben.")
      })
  public Boolean isNachlieferungsbezirk(
      @PathVariable("wahltagID") String wahltagID,
      @PathVariable("wahlbezirkID") String wahlbezirkID) {
    return nachlieferungsbezirkeService.isNachlieferungsbezirk(wahltagID, wahlbezirkID);
  }

  @PostMapping("{wahltagID}")
  @Operation(
      description =
          "Speichern der Liste an Nachlieferungsbezirken für einen bestimmten Wahltag. Eine bestehende Liste wird ersetzt.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Nachlieferungsbezirke erfolgreich gespeichert")
      })
  public void setNachlieferungsbezirke(
      @PathVariable("wahltagID") String wahltagID, final MultipartHttpServletRequest request) {
    final List<String> requestContent;
    try {
      requestContent = fileMapper.readNachlieferungsbezirke(request);
    } catch (final IOException e) {
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.POSTNACHLIEFERUNGSBEZIRKE_READ_FILE_NICHT_ERFOLGREICH);
    }
    nachlieferungsbezirkeService.setNachlieferungsbezirke(wahltagID, requestContent);
  }
}
