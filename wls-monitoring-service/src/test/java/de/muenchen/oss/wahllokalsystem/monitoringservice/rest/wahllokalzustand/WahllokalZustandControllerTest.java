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

    //    @Nested
    //    class PostSchnellmeldungsSendungsuhrzeit {
    //
    //        @Test
    //        void should_notThrowException_when_serviceIsCalled() {
    //            val requestBody = Mockito.mock(SendungsdatenDTO.class);
    //            val mockedSendungsdatenModel = Mockito.mock(SendungsdatenModel.class);
    //
    //            Mockito.when(sendungsdatenDTOMapper.toSendungsdatenModel(requestBody)).thenReturn(mockedSendungsdatenModel);
    //
    //            unitUnderTest.postSchnellmeldungsSendungsuhrzeit(requestBody);
    //            Mockito.verify(wahllokalZustandService).postSchnellmeldungSendungsuhrzeit(mockedSendungsdatenModel);
    //        }
    //    }
    //
    //    @Nested
    //    class PostSchnellmeldungDruckuhrzeit {
    //
    //        @Test
    //        void should_notThrowException_when_serviceIsCalled() {
    //            val requestBody = Mockito.mock(DruckdatenDTO.class);
    //            val mockedDruckdatenModel = Mockito.mock(DruckdatenModel.class);
    //
    //            Mockito.when(druckdatenDTOMapper.toDruckdatenModel(requestBody)).thenReturn(mockedDruckdatenModel);
    //
    //            unitUnderTest.postSchnellmeldungDruckuhrzeit(requestBody);
    //            Mockito.verify(wahllokalZustandService).postSchnellmeldungDruckuhrzeit(mockedDruckdatenModel);
    //        }
    //    }
    //
    //    @Nested
    //    class PostNiederschriftSendungsuhrzeit {
    //
    //        @Test
    //        void should_notThrowException_when_serviceIsCalled() {
    //            val requestBody = Mockito.mock(SendungsdatenDTO.class);
    //            val mockedSendungsdatenModel = Mockito.mock(SendungsdatenModel.class);
    //
    //            Mockito.when(sendungsdatenDTOMapper.toSendungsdatenModel(requestBody)).thenReturn(mockedSendungsdatenModel);
    //
    //            unitUnderTest.postNiederschriftSendungsuhrzeit(requestBody);
    //            Mockito.verify(wahllokalZustandService).postNiederschriftSendungsuhrzeit(mockedSendungsdatenModel);
    //        }
    //    }
    //
    //    @Nested
    //    class PostNiederschriftDruckuhrzeit {
    //
    //        @Test
    //        void should_notThrowException_when_serviceIsCalled() {
    //            val requestBody = Mockito.mock(DruckdatenDTO.class);
    //            val mockedDruckdatenModel = Mockito.mock(DruckdatenModel.class);
    //
    //            Mockito.when(druckdatenDTOMapper.toDruckdatenModel(requestBody)).thenReturn(mockedDruckdatenModel);
    //
    //            unitUnderTest.postNiederschriftDruckuhrzeit(requestBody);
    //            Mockito.verify(wahllokalZustandService).postNiederschriftDruckuhrzeit(mockedDruckdatenModel);
    //        }
    //    }
}
