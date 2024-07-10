package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class HandbuchControllerTest {

    @Mock
    HandbuchService handbuchService;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    HandbuchDTOMapper handbuchDTOMapper;

    @InjectMocks
    HandbuchController unitUnderTest;

    @Nested
    class GetHandbuch {

        @Test
        void serviceIsCalled() {
            val filenameSuffix = "file.pdf";
            val wahltagID = "wahltagID";
            val wahlbezirkArt = WahlbezirkArtDTO.UWB;

            val mockedHandbuchReferenceModel = HandbuchReferenceModel.builder().build();
            val mockedServiceResponse = "response".getBytes();

            Mockito.when(handbuchDTOMapper.toModel(eq(wahltagID), eq(wahlbezirkArt))).thenReturn(mockedHandbuchReferenceModel);
            Mockito.when(handbuchService.getHandbuch(mockedHandbuchReferenceModel)).thenReturn(mockedServiceResponse);

            unitUnderTest.manualFileNameSuffix = filenameSuffix;

            val result = unitUnderTest.getHandbuch(wahltagID, wahlbezirkArt);

            val expectedHeaders = new HttpHeaders();
            expectedHeaders.add("Content-Type", "application/pdf");
            expectedHeaders.add("Content-Disposition", "attachment; filename=UWB" + filenameSuffix);
            val expectedResult = new ResponseEntity<byte[]>(mockedServiceResponse, expectedHeaders, HttpStatus.OK);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class SetHandbuch {

        @Test
        void requestIsSendToService() {
            val fileContent = "helloMyLovelyTestcase".getBytes();
            val multiPartFiles = new LinkedMultiValueMap<String, MultipartFile>();
            multiPartFiles.put("key", List.of(new MockMultipartFile("filename", fileContent)));
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest, multiPartFiles, null, null);

            unitUnderTest.setHandbuch(null, null, servletRequest);
        }

        @Test
        void exceptionWhenRequestHasNoAttachment() {

        }

        @Test
        void anyServiceExceptionIsMappedToTechnischeWlsException() {

        }
    }

}