package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

class FileMapperTest {

    private final FileMapper unitUnderTest = new FileMapper();

    @Nested
    class ToResponseEntity {

        @Test
        void responseEntityIsBuilt() {
            val body = "content".getBytes();
            val attachmentFilename = "attachment.txt";
            val contentType = "text/plain";

            val result = unitUnderTest.toResponseEntity(new FileResponseEntityModel(body, contentType, attachmentFilename));

            Assertions.assertThat(result.getBody()).isEqualTo(body);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType(contentType));
            Assertions.assertThat(result.getHeaders().getContentDisposition())
                    .isEqualTo(ContentDisposition.builder("attachment").filename(attachmentFilename).build());
            Assertions.assertThat(result.getHeaders().size()).isEqualTo(2);
        }
    }

    @Nested
    class FromRequest {

        @Test
        void gotDataFromRequest() throws IOException {
            val fileContent = "content".getBytes();
            val multiPartFiles = new LinkedMultiValueMap<String, MultipartFile>();
            multiPartFiles.put("key", List.of(new MockMultipartFile("filename", fileContent)));
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest, multiPartFiles, null, null);

            val result = unitUnderTest.fromRequest(servletRequest);

            Assertions.assertThat(result).isEqualTo(fileContent);
        }

        @Test
        void exceptionWhenRequestHasNoFile() {
            val multiPartFiles = new LinkedMultiValueMap<String, MultipartFile>();
            multiPartFiles.put("key", Collections.emptyList());
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest, multiPartFiles, null, null);

            Assertions.assertThatThrownBy(() -> unitUnderTest.fromRequest(servletRequest)).isInstanceOf(IOException.class);
        }
    }

}
