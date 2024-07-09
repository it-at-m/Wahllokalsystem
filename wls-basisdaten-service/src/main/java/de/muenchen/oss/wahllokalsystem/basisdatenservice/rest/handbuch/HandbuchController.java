package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchWriteModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

    private final HandbuchService handbuchService;

    private final ExceptionFactory exceptionFactory;

    @GetMapping("{wahltagID}/{wahlbezirksart}")
    public void getHandbuch(@PathVariable("wahltagID") String wahltagID, @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO,
            HttpServletResponse response) {

        val handbuchData = handbuchService.getHandbuch(
                new HandbuchReferenceModel(wahltagID, WahlbezirkArtModel.valueOf(wahlbezirkArtDTO.name()))); //TODO Mapper verwenden

        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + wahlbezirkArtDTO + "Handbuch.pdf");
            response.getOutputStream().write(handbuchData);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("#getHandbuch: Bei der Verarbeitung des Handbuches ist ein Fehler aufgetreten: {}", e);
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e1) {
                log.error("#getHandbuch: IOException by sendError {}", e1);
            }
        }
    }

    @PostMapping("{wahltagID}/{wahlbezirksart}")
    public void getHandbuch(@PathVariable("wahltagID") String wahltagID, @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO,
            MultipartHttpServletRequest request) {
        val itr = request.getFileNames();
        val file = request.getFile(itr.next());

        try {
            handbuchService.setHandbuch(new HandbuchWriteModel(new HandbuchReferenceModel(wahltagID, WahlbezirkArtModel.valueOf(wahlbezirkArtDTO.name())),
                    file.getBytes()));
        } catch (final IOException e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH);
        }
    }
}
