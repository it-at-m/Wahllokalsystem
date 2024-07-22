package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component
@Slf4j
public class FileMapper {

    public ResponseEntity<byte[]> toResponseEntity(final FileResponseEntityModel fileResponseEntityModel) {
        val responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, fileResponseEntityModel.headerContentType());
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResponseEntityModel.attachmentFilename());

        return new ResponseEntity<>(fileResponseEntityModel.responseBody(), responseHeaders, HttpStatus.OK);
    }

    public byte[] fromRequest(final MultipartHttpServletRequest request) throws IOException {
        val fileName = request.getFileNames().next();
        log.debug("using filename > {}", fileName);
        val file = request.getFile(fileName);

        if (file == null) {
            throw new IOException("No file was uploaded");
        }

        return file.getBytes();
    }
}
