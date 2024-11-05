package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandControllerTest {

    @Mock
    WahllokalZustandService wahllokalZustandService;

    @InjectMocks
    WahllokalZustandController unitUnderTest;

    @Nested
    class PostLastSeen {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            String wahlbezirkID = "wahlbezirkID01";
            unitUnderTest.postLastSeen(wahlbezirkID);
            Mockito.verify(wahllokalZustandService).postLastSeen(wahlbezirkID);
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            String wahlbezirkID = "wahlbezirkID01";
            unitUnderTest.postLetzteAbmeldung(wahlbezirkID);
            Mockito.verify(wahllokalZustandService).postLetzteAbmeldung(wahlbezirkID);
        }
    }

    @Nested
    class PostSchnellmeldungsSendungsuhrzeit {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            val requestBody = Mockito.mock(SendungsdatenDTO.class);
            unitUnderTest.postSchnellmeldungsSendungsuhrzeit(requestBody);
            Mockito.verify(wahllokalZustandService).postSchnellmeldungSendungsuhrzeit(requestBody.bezirkUndWahlID(), requestBody.sendungsuhrzeit());
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            val requestBody = Mockito.mock(DruckdatenDTO.class);
            unitUnderTest.postSchnellmeldungDruckuhrzeit(requestBody);
            Mockito.verify(wahllokalZustandService).postSchnellmeldungDruckuhrzeit(requestBody.bezirkUndWahlID(), requestBody.druckuhrzeit());
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            val requestBody = Mockito.mock(SendungsdatenDTO.class);
            unitUnderTest.postNiederschriftSendungsuhrzeit(requestBody);
            Mockito.verify(wahllokalZustandService).postNiederschriftSendungsuhrzeit(requestBody.bezirkUndWahlID(), requestBody.sendungsuhrzeit());
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            val requestBody = Mockito.mock(DruckdatenDTO.class);
            unitUnderTest.postNiederschriftDruckuhrzeit(requestBody);
            Mockito.verify(wahllokalZustandService).postNiederschriftDruckuhrzeit(requestBody.bezirkUndWahlID(), requestBody.druckuhrzeit());
        }
    }
}
