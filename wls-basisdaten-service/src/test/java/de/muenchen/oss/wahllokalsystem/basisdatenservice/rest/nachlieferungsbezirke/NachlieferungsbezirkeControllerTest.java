package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.nachlieferungsbezirke;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.FileMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.nachlieferungsbezirke.NachlieferungsbezirkeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class NachlieferungsbezirkeControllerTest {

  @Mock NachlieferungsbezirkeService nachlieferungsbezirkeService;

  @Mock FileMapper fileMapper;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks NachlieferungsbezirkeController unitUnderTest;

  @Nested
  class CheckForNachlieferungsbezirk {

    @Test
    void should_callService_when_callingCheckForNachlieferungsbezirk() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";
      val mockedServiceResponse = false;

      Mockito.when(
              nachlieferungsbezirkeService.checkForNachlieferungsbezirk(wahltagID, wahlbezirkID))
          .thenReturn(mockedServiceResponse);

      val result = unitUnderTest.checkForNachlieferungsbezirk(wahltagID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(mockedServiceResponse);
    }
  }

  @Nested
  class SetNachlieferungsbezirke {

    @Test
    void should_callService_when_callingSetNachlieferungsbezirke() throws IOException {
      val wahltagID = "wahltagID";
      val nachlieferungsbezirkeList =
          Arrays.asList("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");
      final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
      val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

      Mockito.when(fileMapper.readNachlieferungsbezirke(servletRequest))
          .thenReturn(nachlieferungsbezirkeList);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.setNachlieferungsbezirke(wahltagID, servletRequest));

      Mockito.verify(nachlieferungsbezirkeService)
          .setNachlieferungsbezirke(wahltagID, nachlieferungsbezirkeList);
    }

    @Test
    void should_mapToWlsException_when_ioExceptionOccurs() throws IOException {
      val wahltagID = "wahltagID";
      final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
      val servletRequest = new DefaultMultipartHttpServletRequest(httpServletRequest);

      val mockedFileMapperIOException = new IOException("ioException of fileMapper");
      val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

      Mockito.doThrow(mockedFileMapperIOException)
          .when(fileMapper)
          .readNachlieferungsbezirke(servletRequest);
      Mockito.when(
              exceptionFactory.createTechnischeWlsException(
                  ExceptionConstants.POSTNACHLIEFERUNGSBEZIRKE_SPEICHERN_NICHT_ERFOLGREICH))
          .thenReturn(mockedWlsException);

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, servletRequest))
          .isSameAs(mockedWlsException);

      Mockito.verify(nachlieferungsbezirkeService, times(0)).setNachlieferungsbezirke(any(), any());
    }
  }
}
