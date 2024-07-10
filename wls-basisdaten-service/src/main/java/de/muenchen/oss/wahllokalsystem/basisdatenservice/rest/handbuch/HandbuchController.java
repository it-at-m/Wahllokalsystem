package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/businessActions/handbuch")
@RequiredArgsConstructor
@Slf4j
public class HandbuchController {

    @Value("${service.config.manual.filenamesuffix:Handbuch.pdf}")
    String manualFileNameSuffix;

    private final HandbuchService handbuchService;

    private final ExceptionFactory exceptionFactory;

    private final HandbuchDTOMapper handbuchDTOMapper;

    @GetMapping("{wahltagID}/{wahlbezirksart}")
    @Operation(
            description = "Abrufen des Handbuches für eine Wahl für eine bestimmte Wahlbezirksart",
            responses = {
                    @ApiResponse(
                            responseCode = "500", description = "Handbuch ist nicht abrufbar. Entweder fehlt es oder es gab technische Probleme",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    public ResponseEntity<byte[]> getHandbuch(@PathVariable("wahltagID") String wahltagID,
            @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO) {
        val handbuchData = handbuchService.getHandbuch(handbuchDTOMapper.toModel(wahltagID, wahlbezirkArtDTO));
        return createPDFResponse(handbuchData, wahlbezirkArtDTO);
    }

    @PostMapping("{wahltagID}/{wahlbezirksart}")
    @Operation(
            description = "Speichern eines Handbuches für eine Wahl für eine bestimmte Wahlbezirksart",
            responses = {
                    @ApiResponse(responseCode = "500", description = "Handbuch nicht speicherbar"),
                    @ApiResponse(
                            responseCode = "400", description = "Anfrageparameter sind fehlerhaft",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WlsExceptionDTO.class))
                    )
            }
    )
    public void setHandbuch(@PathVariable("wahltagID") String wahltagID, @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO,
            final MultipartHttpServletRequest request) {
        try {
            val handbuchData = getHandbuchFromRequest(request);
            val modelToSet = handbuchDTOMapper.toModel(handbuchDTOMapper.toModel(wahltagID, wahlbezirkArtDTO), handbuchData);
            handbuchService.setHandbuch(modelToSet);
        } catch (final IOException e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH);
        }
    }

    private byte[] getHandbuchFromRequest(final MultipartHttpServletRequest request) throws IOException {
        val fileName = request.getFileNames().next();
        log.debug("using filename > {}", fileName);
        val file = request.getFile(fileName);

        if (file == null) {
            throw new IOException("No file was uploaded");
        }

        return file.getBytes();
    }

    private ResponseEntity<byte[]> createPDFResponse(final byte[] responseBody, final WahlbezirkArtDTO wahlbezirkArtDTO) {
        val responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + wahlbezirkArtDTO + manualFileNameSuffix);

        return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
    }
}
