package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.ungueltigewahlscheine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileResponseEntityModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineWriteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class UngueltigeWahlscheineControllerTest {

    @Mock
    UngueltigeWahlscheineService ungueltigeWahlscheineService;

    @Mock
    UngueltigeWahlscheineDTOMapper ungueltigeWahlscheineDTOMapper;

    @Mock
    FileMapper fileMapper;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    UngueltigeWahlscheineController unitUnderTest;

    @Nested
    class GetUngueltigeWahlscheine {

        @BeforeEach
        void setUp() {
            unitUnderTest.ungueltigeWahlscheineFileNameSuffix = ".csv";
        }

        @Test
        void serviceCalled() {
            val wahltagID = "wahltagID";

            val mockedReferenceModel = new UngueltigeWahlscheineReferenceModel(wahltagID, WahlbezirkArtModel.BWB);
            val mockedServiceResponse = "serviceData".getBytes();
            val mockedFileMapperResponse = ResponseEntity.ok(mockedServiceResponse);

            Mockito.when(ungueltigeWahlscheineDTOMapper.toModel(wahltagID, WahlbezirkArtDTO.BWB)).thenReturn(mockedReferenceModel);
            Mockito.when(ungueltigeWahlscheineService.getUngueltigeWahlscheine(mockedReferenceModel)).thenReturn(mockedServiceResponse);
            Mockito.when(fileMapper.toResponseEntity(new FileResponseEntityModel(mockedServiceResponse, "text/csv", "BWB.csv")))
                    .thenReturn(mockedFileMapperResponse);

            val result = unitUnderTest.getUngueltigeWahlscheine(wahltagID, WahlbezirkArtDTO.BWB);

            Assertions.assertThat(result).isEqualTo(mockedFileMapperResponse);
        }

    }

    @Nested
    class SetUngueltigeWahlscheine {

        @Test
        void serviceCalled() throws IOException {
            val wahltagID = "wahltagID";
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

            val mockedServletBody = "data".getBytes();
            val mockedServiceWriteModel = UngueltigeWahlscheineWriteModel.builder().build();
            val mockedReferenceModel = UngueltigeWahlscheineReferenceModel.builder().build();

            Mockito.when(fileMapper.fromRequest(servletRequest)).thenReturn(mockedServletBody);
            Mockito.when(ungueltigeWahlscheineDTOMapper.toModel(eq(wahltagID), eq(WahlbezirkArtDTO.UWB))).thenReturn(mockedReferenceModel);
            Mockito.when(ungueltigeWahlscheineDTOMapper.toModel(eq(mockedReferenceModel), eq(mockedServletBody))).thenReturn(mockedServiceWriteModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(wahltagID, WahlbezirkArtDTO.UWB, servletRequest));

            Mockito.verify(ungueltigeWahlscheineService).setUngueltigeWahlscheine(mockedServiceWriteModel);
        }

        @Test
        void occuringIoExceptionIsMappedToWlsException() throws IOException {
            val wahltagID = "wahltagID";
            final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

            val mockedFileMapperIOException = new IOException("ioException of fileMapper");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.doThrow(mockedFileMapperIOException).when(fileMapper).fromRequest(servletRequest);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTUNGUELTIGEWS_SPEICHERN_NICHT_ERFOLGREICH))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setUngueltigeWahlscheine(wahltagID, WahlbezirkArtDTO.UWB, servletRequest))
                    .isSameAs(mockedWlsException);

            Mockito.verify(ungueltigeWahlscheineService, times(0)).setUngueltigeWahlscheine(any());
        }
    }

}
