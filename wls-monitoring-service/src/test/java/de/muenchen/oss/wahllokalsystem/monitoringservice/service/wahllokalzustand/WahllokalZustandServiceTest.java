package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;

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
class WahllokalZustandServiceTest {

    @Mock
    WahllokalZustandValidator wahllokalZustandValidator;
    @Mock
    WahllokalZustandClient wahllokalZustandClient;
    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahllokalZustandService unitUnderTest;

    @Nested
    class PostLastSeen {

        @Test
        void should_notThrowException_when_wahlbezirkIDIsGiven() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postLastSeen(wahlbezirkID));
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_notThrowException_when_wahlbezirkIDIsGiven() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postLetzteAbmeldung(wahlbezirkID));
        }
    }

    @Nested
    class PostSchnellmeldungSendungsuhrzeit {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsGiven() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), null));
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsGiven() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), null));
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsGiven() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), null));
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsGiven() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("123").buildWithMessage("Abc");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), null));
        }
    }
}
