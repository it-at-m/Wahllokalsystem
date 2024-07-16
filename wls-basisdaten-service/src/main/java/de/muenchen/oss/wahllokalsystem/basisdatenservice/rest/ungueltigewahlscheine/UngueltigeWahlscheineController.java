package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileResponseEntityModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/businessActions/ungueltigews")
@RequiredArgsConstructor
public class UngueltigeWahlscheineController {

    private static final String UNGUELTIGE_WAHLSCHEINE_FILE_CONTENT_TYPE = "text/csv";

    @Value("${service.config.ungueltigewahlscheine.filenamesuffix:Ungueltigews.csv}")
    String ungueltigeWahlscheineFileNameSuffix;

    private final UngueltigeWahlscheineService ungueltigeWahlscheineService;

    private final UngueltigeWahlscheineDTOMapper ungueltigeWahlscheineDTOMapper;

    private final FileMapper fileMapper;

    private final ExceptionFactory exceptionFactory;

    @GetMapping("{wahltagID}/{wahlbezirksart}")
    public ResponseEntity<byte[]> getUngueltigeWahlscheine(@PathVariable("wahltagID") String wahltagID,
            @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO) {
        val ungueltigeWahlscheineData = ungueltigeWahlscheineService.getUngueltigeWahlscheine(
                ungueltigeWahlscheineDTOMapper.toModel(wahltagID, wahlbezirkArtDTO));

        val attachmentFilename = wahlbezirkArtDTO.toString() + ungueltigeWahlscheineFileNameSuffix;

        return fileMapper.toResponseEntity(
                new FileResponseEntityModel(ungueltigeWahlscheineData, UNGUELTIGE_WAHLSCHEINE_FILE_CONTENT_TYPE, attachmentFilename));
    }

    @PostMapping("{wahltagID}/{wahlbezirksart}")
    public void setUngueltigeWahlscheine(@PathVariable("wahltagID") String wahltagID,
            @PathVariable("wahlbezirksart") WahlbezirkArtDTO wahlbezirkArtDTO, final MultipartHttpServletRequest request) {
        try {
            val requestContent = fileMapper.fromRequest(request);
            val ungueltigeWahlscheineWriteModel = ungueltigeWahlscheineDTOMapper.toModel(ungueltigeWahlscheineDTOMapper.toModel(wahltagID, wahlbezirkArtDTO),
                    requestContent);
            ungueltigeWahlscheineService.setUngueltigeWahlscheine(ungueltigeWahlscheineWriteModel);
        } catch (final IOException e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTUNGUELTIGEWS_SPEICHERN_NICHT_ERFOLGREICH);
        }
    }
}
