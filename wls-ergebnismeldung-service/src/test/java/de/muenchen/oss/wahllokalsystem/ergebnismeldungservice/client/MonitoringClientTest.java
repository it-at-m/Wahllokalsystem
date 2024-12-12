package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.client.WahllokalZustandControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.DruckdatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.SendungsdatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonitoringClientTest {

    @Mock
    WahllokalZustandControllerApi wahllokalZustandControllerApi;

    @Mock
    StatusClientMapper statusClientMapper;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    MonitoringClient unitUnderTest;

    @Nested
    class PostSchnellmeldungSendungsuhrzeit {

        @Test
        void should_sendDataViaController_when_dataIsGiven() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);

            unitUnderTest.postSchnellmeldungSendungsuhrzeit(bezirkUndWahlID, uhrzeit);

            Mockito.verify(wahllokalZustandControllerApi).postSchnellmeldungSendungsuhrzeit(mockedDTOToSend);
        }

        @Test
        void should_rethrowWlsException_when_apiThrowsWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            val mockedApiWlsExcepton = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiWlsExcepton).when(wahllokalZustandControllerApi).postSchnellmeldungSendungsuhrzeit(mockedDTOToSend);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postSchnellmeldungSendungsuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedApiWlsExcepton);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrowsNonWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            val mockedApiException = new RuntimeException("api call failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiException).when(wahllokalZustandControllerApi).postSchnellmeldungSendungsuhrzeit(mockedDTOToSend);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_MONITORING)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postSchnellmeldungSendungsuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_sendDataViaController_when_dataIsGiven() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);

            unitUnderTest.postSchnellmeldungDruckuhrzeit(bezirkUndWahlID, uhrzeit);

            Mockito.verify(wahllokalZustandControllerApi).postSchnellmeldungDruckuhrzeit(mockedDTOToSend);
        }

        @Test
        void should_rethrowWlsException_when_apiThrowsWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            val mockedApiWlsExcepton = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiWlsExcepton).when(wahllokalZustandControllerApi).postSchnellmeldungDruckuhrzeit(mockedDTOToSend);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postSchnellmeldungDruckuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedApiWlsExcepton);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrowsNonWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            val mockedApiException = new RuntimeException("api call failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiException).when(wahllokalZustandControllerApi).postSchnellmeldungDruckuhrzeit(mockedDTOToSend);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_MONITORING)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postSchnellmeldungDruckuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_sendDataViaController_when_dataIsGiven() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);

            unitUnderTest.postNiederschriftSendungsuhrzeit(bezirkUndWahlID, uhrzeit);

            Mockito.verify(wahllokalZustandControllerApi).postNiederschriftSendungsuhrzeit(mockedDTOToSend);
        }

        @Test
        void should_rethrowWlsException_when_apiThrowsWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            val mockedApiWlsExcepton = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiWlsExcepton).when(wahllokalZustandControllerApi).postNiederschriftSendungsuhrzeit(mockedDTOToSend);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postNiederschriftSendungsuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedApiWlsExcepton);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrowsNonWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(SendungsdatenDTO.class);
            val mockedApiException = new RuntimeException("api call failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toSendungsdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiException).when(wahllokalZustandControllerApi).postNiederschriftSendungsuhrzeit(mockedDTOToSend);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_MONITORING)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postNiederschriftSendungsuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_sendDataViaController_when_dataIsGiven() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);

            unitUnderTest.postNiederschriftDruckuhrzeit(bezirkUndWahlID, uhrzeit);

            Mockito.verify(wahllokalZustandControllerApi).postNiederschriftDruckuhrzeit(mockedDTOToSend);
        }

        @Test
        void should_rethrowWlsException_when_apiThrowsWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            val mockedApiWlsExcepton = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiWlsExcepton).when(wahllokalZustandControllerApi).postNiederschriftDruckuhrzeit(mockedDTOToSend);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedApiWlsExcepton);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrowsNonWlsException() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val uhrzeit = LocalDateTime.now();

            val mockedDTOToSend = Mockito.mock(DruckdatenDTO.class);
            val mockedApiException = new RuntimeException("api call failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("api call failed");

            Mockito.when(statusClientMapper.toDruckdatenDTO(eq(bezirkUndWahlID), eq(uhrzeit))).thenReturn(mockedDTOToSend);
            Mockito.doThrow(mockedApiException).when(wahllokalZustandControllerApi).postNiederschriftDruckuhrzeit(mockedDTOToSend);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_MONITORING)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(bezirkUndWahlID, uhrzeit))
                    .isSameAs(mockedWlsException);
        }
    }
}
