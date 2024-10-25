package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahllokalzustandControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckzustandModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandOperation;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
    WahllokalZustandClientMapper wahllokalZustandClientMapper;

    @InjectMocks
    WahllokalZustandClientImpl unitUnderTest;

    @Nested
    class PostWahllokalZustand {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithModel() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = OffsetDateTime.now();
            for (WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                val wahllokalzustandModel = createWahllokalZustandModel(operation, wahlID, wahlbezirkID, zeitpunkt);
                val mockedWahllokalzustandDTO = createWahllokalZustandDTO(operation, wahlID, wahlbezirkID, zeitpunkt);
                Mockito.when(wahllokalZustandClientMapper.toDTO(wahllokalzustandModel)).thenReturn(mockedWahllokalzustandDTO);

                unitUnderTest.postWahllokalZustand(wahllokalzustandModel);
                Mockito.verify(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalzustandDTO);
            }
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {
            val zeitpunkt = OffsetDateTime.now();
            val wahllokalZustandModel = createWahllokalZustandModel(WahllokalZustandOperation.POST_LASTSEEN, "wahlID", "wahlbezirkID", zeitpunkt);
            val mockedWahllokalZustandDTO = createWahllokalZustandDTO(WahllokalZustandOperation.POST_LASTSEEN, "wahlID", "wahlbezirkID", zeitpunkt);
            Mockito.when(wahllokalZustandClientMapper.toDTO(wahllokalZustandModel)).thenReturn(mockedWahllokalZustandDTO);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahllokalzustandControllerApi).saveWahllokalZustand(mockedWahllokalZustandDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahllokalZustand(wahllokalZustandModel)).isSameAs(mockedWlsException);
        }

        private WahllokalZustandDTO createWahllokalZustandDTO(WahllokalZustandOperation wahllokalZustandOperation, final String wahlID,
                final String wahlbezirkID, OffsetDateTime zeitpunkt) {
            return switch (wahllokalZustandOperation) {
            case POST_LASTSEEN -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).zuletztGesehen(zeitpunkt);
            case POST_LETZTEABMELDUNG -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).letzteAbmeldung(zeitpunkt);
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .schnellmeldungSendenUhrzeit(zeitpunkt)));
            case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .schnellmeldungDruckUhrzeit(zeitpunkt)));
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .niederschriftSendenUhrzeit(zeitpunkt)));
            case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .niederschriftDruckUhrzeit(zeitpunkt)));

            };
        }

        private WahllokalZustandModel createWahllokalZustandModel(WahllokalZustandOperation wahllokalZustandOperation, final String wahlID,
                final String wahlbezirkID, OffsetDateTime zeitpunkt) {
            WahllokalZustandModel.builder().build();
            return switch (wahllokalZustandOperation) {
            case POST_LASTSEEN -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID).zuletztGesehen(zeitpunkt.toLocalDateTime()).build();
            case POST_LETZTEABMELDUNG -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID).letzteAbmeldung(zeitpunkt.toLocalDateTime()).build();
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .schnellmeldungSendenUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_SCHNELLMELDUNG_DRUCKUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .schnellmeldungDruckUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .niederschriftSendenUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_NIEDERSCHRIFT_DRUCKUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .niederschriftDruckUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            };
        }

    }
}
