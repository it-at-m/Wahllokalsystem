package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.common.TimeStampMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahllokalzustandControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
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
class WahllokalZustandClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahllokalzustandControllerApi wahllokalzustandControllerApi;

    @Mock
    TimeStampMapper timeStampMapper;

    @InjectMocks
    WahllokalZustandClientImpl unitUnderTest;

    @Nested
    class PostLastSeen {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithWahlbezirkIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_LASTSEEN", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postLastSeen(wahlbezirkID, zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_LASTSEEN", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postLastSeen(wahlbezirkID, zeitpunkt)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithWahlbezirkIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_LETZTEABMELDUNG", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postLetzteAbmeldung(wahlbezirkID, zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_LETZTEABMELDUNG", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postLetzteAbmeldung(wahlbezirkID, zeitpunkt)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostSchnellmeldungSendungsuhrzeithrzeit {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithBezirUndWahlIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithBezirUndWahlIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_SCHNELLMELDUNG_DRUCKUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_SCHNELLMELDUNG_DRUCKUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithBezirUndWahlIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithBezirUndWahlIDAndUhrzeit() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("POST_NIEDERSCHRIFT_DRUCKUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            unitUnderTest.postNiederschriftDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();
            val zeitpunktOffset = OffsetDateTime.now();

            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("POST_NIEDERSCHRIFT_DRUCKUHRZEIT", wahlID, wahlbezirkID, zeitpunktOffset);
            Mockito.when(timeStampMapper.localDateTimeToOffsetDateTime(zeitpunkt)).thenReturn(zeitpunktOffset);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), zeitpunkt))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostWahllokalZustand {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithModel() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = OffsetDateTime.now();

            val mockedWahllokalzustandDTO = createWahllokalZustandDTO("emptyDTO", wahlID, wahlbezirkID, zeitpunkt);

            unitUnderTest.postWahllokalZustand(mockedWahllokalzustandDTO);
            Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val zeitpunkt = OffsetDateTime.now();
            val mockedWahllokalZustandDTO = createWahllokalZustandDTO("emptyDTO", "wahlID", "wahlbezirkID", zeitpunkt);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahllokalZustand(mockedWahllokalZustandDTO)).isSameAs(mockedWlsException);
        }

    }

    private WahllokalZustandDTO createWahllokalZustandDTO(final String wahllokalZustandOperation, final String wahlID,
            final String wahlbezirkID, OffsetDateTime zeitpunkt) {
        return switch (wahllokalZustandOperation) {
        case "POST_LASTSEEN" -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).zuletztGesehen(zeitpunkt);
        case "POST_LETZTEABMELDUNG" -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).letzteAbmeldung(zeitpunkt);
        case "POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT" ->
            new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    new DruckzustandDTO()
                                            .wahlID(wahlID)
                                            .schnellmeldungSendenUhrzeit(zeitpunkt)));
        case "POST_SCHNELLMELDUNG_DRUCKUHRZEIT" ->
            new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    new DruckzustandDTO()
                                            .wahlID(wahlID)
                                            .schnellmeldungDruckUhrzeit(zeitpunkt)));
        case "POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT" ->
            new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    new DruckzustandDTO()
                                            .wahlID(wahlID)
                                            .niederschriftSendenUhrzeit(zeitpunkt)));
        case "POST_NIEDERSCHRIFT_DRUCKUHRZEIT" ->
            new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    new DruckzustandDTO()
                                            .wahlID(wahlID)
                                            .niederschriftDruckUhrzeit(zeitpunkt)));

        default -> new WahllokalZustandDTO();
        };
    }
}
