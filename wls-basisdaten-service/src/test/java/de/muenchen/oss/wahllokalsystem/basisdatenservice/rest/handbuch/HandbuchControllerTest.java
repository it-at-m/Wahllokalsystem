package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileResponseEntityModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchWriteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class HandbuchControllerTest {

    @Mock
    HandbuchService handbuchService;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    HandbuchDTOMapper handbuchDTOMapper;

    @Mock
    FileMapper fileMapper;

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
            val mockedResponseEntity = ResponseEntity.ok(mockedServiceResponse);

            Mockito.when(handbuchDTOMapper.toModel(eq(wahltagID), eq(wahlbezirkArt))).thenReturn(mockedHandbuchReferenceModel);
            Mockito.when(handbuchService.getHandbuch(mockedHandbuchReferenceModel)).thenReturn(mockedServiceResponse);
            Mockito.when(fileMapper.toResponseEntity(new FileResponseEntityModel(mockedServiceResponse, "application/pdf", "UWBfile.pdf")))
                    .thenReturn(mockedResponseEntity);

            unitUnderTest.manualFileNameSuffix = filenameSuffix;

            val result = unitUnderTest.getHandbuch(wahltagID, wahlbezirkArt);

            Assertions.assertThat(result).isEqualTo(mockedResponseEntity);
        }
    }

    @Nested
    class SetHandbuch {

        @Test
        void requestIsSendToService() throws IOException {
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

            val mockedHandbuchReferenceModel = HandbuchReferenceModel.builder().build();
            val mockedHandbuchWriteModel = HandbuchWriteModel.builder().build();
            val mockedRequestContent = "helloMyLovelyTestcase".getBytes();

            Mockito.when(handbuchDTOMapper.toModel(eq("wahltagID"), eq(WahlbezirkArtDTO.UWB))).thenReturn(mockedHandbuchReferenceModel);
            Mockito.when(handbuchDTOMapper.toModel(eq(mockedHandbuchReferenceModel), eq(mockedRequestContent))).thenReturn(mockedHandbuchWriteModel);
            Mockito.when(fileMapper.fromRequest(servletRequest)).thenReturn(mockedRequestContent);

            unitUnderTest.setHandbuch("wahltagID", WahlbezirkArtDTO.UWB, servletRequest);

            Mockito.verify(handbuchService).setHandbuch(mockedHandbuchWriteModel);
        }

        @Test
        void ioExceptionIsMappedToWlsException() throws IOException {
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

            val mockedFileMapperException = new IOException("ioException of fileMapper");
            val mockedMappedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.doThrow(mockedFileMapperException).when(fileMapper).fromRequest(servletRequest);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH))
                    .thenReturn(mockedMappedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setHandbuch("wahltagID", WahlbezirkArtDTO.UWB, servletRequest))
                    .isSameAs(mockedMappedWlsException);
        }
    }

}
