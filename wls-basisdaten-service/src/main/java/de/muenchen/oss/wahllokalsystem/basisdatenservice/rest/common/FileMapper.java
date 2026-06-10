package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.FileResponseEntityModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

  public ResponseEntity<byte[]> toResponseEntity(
      final FileResponseEntityModel fileResponseEntityModel) {
    val responseHeaders = new HttpHeaders();
    responseHeaders.add(HttpHeaders.CONTENT_TYPE, fileResponseEntityModel.headerContentType());
    responseHeaders.add(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + fileResponseEntityModel.attachmentFilename());

    return new ResponseEntity<>(
        fileResponseEntityModel.responseBody(), responseHeaders, HttpStatus.OK);
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

  public List<String> readNachlieferungsbezirke(final MultipartHttpServletRequest request)
      throws IOException {
    val nachlieferungsbezirke = new ArrayList<String>();

    val fileName = request.getFileNames().next();
    log.debug("using filename > {}", fileName);
    val file = request.getFile(fileName);

    if (file == null) {
      throw new IOException("No file was uploaded");
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        nachlieferungsbezirke.add(line);
      }
    }

    nachlieferungsbezirke.removeFirst(); // Header entfernen

    return nachlieferungsbezirke;
  }
}
