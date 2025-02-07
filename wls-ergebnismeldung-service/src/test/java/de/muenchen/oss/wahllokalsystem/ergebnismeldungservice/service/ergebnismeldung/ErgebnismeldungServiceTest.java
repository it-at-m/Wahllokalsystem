package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.Mapping;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ErgebnismeldungValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.MeldungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungServiceTest {

    @Mock
    ErgebnismeldungValidator ergebnismeldungValidator;
    @Mock
    ExceptionFactory exceptionFactory;
    @Mock
    ErgebnismeldungMappingService ergebnismeldungMappingService;
    @Mock
    Mapping mapping;

    @Mock
    UrnenwahlClient urnenwahlClient;
    @Mock
    WahlenClient wahlenClient;
    @Mock
    EaiService eaiService;
    @Mock
    StimmzettelumschlaegeService stimmzettelumschlaegeService;
    @Mock
    StatusService statusService;
    @Mock
    StatusClient statusClient;
    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    ErgebnismeldungService unitUnderTest;

    private final MockedStatic<LocalDateTime> mockedStaticLocalDateTime = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);

    @BeforeEach
    void setup() {
        var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        var mockedLocalDateTime = LocalDateTime.now(clock);
        mockedStaticLocalDateTime.when(LocalDateTime::now).thenReturn(mockedLocalDateTime);
    }

    @AfterEach
    void teardown() {
        mockedStaticLocalDateTime.close();
    }

    @Nested
    class UpdateSendungszeiten {

        @Nested
        class ForUWB {

            @Test
            void should_throwFachlicheWlsException_when_wahlIsNotGeschlossen() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("wahl is nicht geschlossen");

                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHL_NICHT_GESCHLOSSEN)).thenReturn(mockedFachlicheWlsException);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
                Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(false);

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID))
                        .isSameAs(mockedFachlicheWlsException);
            }

            @Test
            void should_sendUpdateSendungszeiten_when_exceptionDuringIsWahlGeschlossenOccurred() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedNow = LocalDateTime.now();
                val mockedUrnenwahlClientException = new RuntimeException("exception on isGeschlossen");
                val mockedStatus = new StatusModel(bezirkUndWahlID, createMeldungWithValidierungsstatus(ValidierungsstatusModel.VALIDE),
                        createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_GESENDET));

                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
                Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedStatus));
                Mockito.doThrow(mockedUrnenwahlClientException).when(urnenwahlClient).isWahlbezirkGeschlossen(wahlbezirkID);

                unitUnderTest.updateSendungszeiten(bezirkUndWahlID);

                Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(eq(bezirkUndWahlID), eq(mockedNow));
            }
        }

        @Nested
        class ForBWB {

            @Test
            void should_throwFachlicheWlsException_when_wahlIsNotGeschlossen() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("wahl is nicht geschlossen");

                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHL_NICHT_GESCHLOSSEN)).thenReturn(mockedFachlicheWlsException);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.BWB);
                Mockito.when(stimmzettelumschlaegeService.getStimmzettelumschlaege(bezirkUndWahlID))
                        .thenReturn(Optional.of(createStimmzettelumschlaegeWithUrnenOeffnungsUhrzeit(null)));

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID))
                        .isSameAs(mockedFachlicheWlsException);
            }

            @Test
            void should_sendUpdateSendungszeiten_when_exceptionDuringIsWahlGeschlossenOccurred() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

                val mockedNow = LocalDateTime.now();
                val mockedStimmzettelumschlaegeService = new RuntimeException("exception on getStimmzettelumschlaege");
                val mockedStatus = new StatusModel(bezirkUndWahlID, createMeldungWithValidierungsstatus(ValidierungsstatusModel.VALIDE),
                        createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_GESENDET));

                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.BWB);
                Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedStatus));
                Mockito.doThrow(mockedStimmzettelumschlaegeService).when(stimmzettelumschlaegeService).getStimmzettelumschlaege(bezirkUndWahlID);

                unitUnderTest.updateSendungszeiten(bezirkUndWahlID);

                Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(eq(bezirkUndWahlID), eq(mockedNow));
            }
        }

        @Test
        void should_sendSchnellmeldungSendungsuhrzeit_when_niederschriftWasNotSendButSchnellmeldungWasSent() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedNow = LocalDateTime.now();
            val mockedStatus = new StatusModel(bezirkUndWahlID, createMeldungWithValidierungsstatus(ValidierungsstatusModel.VALIDE),
                    createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_GESENDET));

            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
            Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedStatus));
            Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);

            unitUnderTest.updateSendungszeiten(bezirkUndWahlID);

            Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(eq(bezirkUndWahlID), eq(mockedNow));
        }

        @Test
        void should_sendNiederschriftSendungsuhrzeit_when_niederschriftWasNotNotSent() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedNow = LocalDateTime.now();
            val mockedStatus = new StatusModel(bezirkUndWahlID, createMeldungWithValidierungsstatus(ValidierungsstatusModel.VALIDE),
                    createMeldungWithValidierungsstatus(ValidierungsstatusModel.VALIDE));

            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
            Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedStatus));
            Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);

            unitUnderTest.updateSendungszeiten(bezirkUndWahlID);

            Mockito.verify(statusClient).postNiederschriftSendungsuhrzeit(eq(bezirkUndWahlID), eq(mockedNow));
        }

        @Test
        void should_throwFachlicheWlsException_when_niederschriftAndSchnellmeldungWasNotSent() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedNow = LocalDateTime.now();
            val mockedStatus = new StatusModel(bezirkUndWahlID, createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_GESENDET),
                    createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_GESENDET));
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
            Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedStatus));
            Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.FORCEERGEBNISSE_WRONG_USAGE)).thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_callValidatorWithBezirkUndWahlID_when_called() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val mockedValidationException = new IllegalArgumentException("validation failed");
            Mockito.doThrow(mockedValidationException).when(ergebnismeldungValidator).validBezirkUndWahlIDOrThrow(bezirkUndWahlID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID)).isSameAs(mockedValidationException);
        }
    }

    @Nested
    class SendErgebnisse {

        @Nested
        class ForUWB {

            @Test
            void should_throwFachlicheWlsException_when_wahlIsNotGeschlossen() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 1L;
                val meldungsart = MeldungsartModel.V1;
                val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart, wahlbezirkID);

                val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("wahl is nicht geschlossen");

                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHL_NICHT_GESCHLOSSEN)).thenReturn(mockedFachlicheWlsException);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.UWB);
                Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(false);
                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria))
                        .isSameAs(mockedFachlicheWlsException);
            }
        }

        @Nested
        class ForBWB {

            @Test
            void should_throwFachlicheWlsException_when_wahlIsNotGeschlossen() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
                val waehlerverzeichnisNummer = 1L;
                val meldungsart = MeldungsartModel.V1;
                val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart, wahlbezirkID);

                val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("wahl is nicht geschlossen");

                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHL_NICHT_GESCHLOSSEN)).thenReturn(mockedFachlicheWlsException);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(WahlbezirkArtModel.BWB);
                Mockito.when(stimmzettelumschlaegeService.getStimmzettelumschlaege(bezirkUndWahlID))
                        .thenReturn(Optional.of(createStimmzettelumschlaegeWithUrnenOeffnungsUhrzeit(null)));

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria))
                        .isSameAs(mockedFachlicheWlsException);
            }
        }

        @Nested
        class ForNiederschrift {

            @Test
            void should_sendSendungsuhrzeiten_when_validErgebnisse() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 1L;
                val meldungsart = MeldungsartModel.V1;
                val hauptwahlbezirkID = "hauptwahlbezirkID";
                val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart,
                        hauptwahlbezirkID);

                val mockedWahlbezirkartOfCurrentUser = WahlbezirkArtModel.UWB;
                val mockedWahlartOfCurrentWahltag = WahlartModel.BTW;
                val mockedEAIMeldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val mockedErgebnismeldungDTO = new ErgebnismeldungDTO().wahlID(wahlID).wahlbezirkID(wahlbezirkID).meldungsart(mockedEAIMeldungsart);
                val mockedNow = LocalDateTime.now();

                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedWahlbezirkartOfCurrentUser);
                Mockito.when(ergebnismeldungValidator.checkValidation(eq(mockedWahlartOfCurrentWahltag), eq(mockedWahlbezirkartOfCurrentUser), eq(wahlbezirkID),
                        eq(wahlID),
                        eq(waehlerverzeichnisNummer), eq(meldungsart))).thenReturn(true);
                Mockito.when(wahlenClient.getWahlartOfCurrentWahltag(wahlID)).thenReturn(mockedWahlartOfCurrentWahltag);
                Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);
                Mockito.when(mapping.toDTO(meldungsart)).thenReturn(mockedEAIMeldungsart);
                Mockito.when(ergebnismeldungMappingService.createErgebnismeldung(eq(mockedWahlartOfCurrentWahltag), eq(wahlID), eq(wahlbezirkID),
                        eq(waehlerverzeichnisNummer), eq(mockedEAIMeldungsart), eq(hauptwahlbezirkID))).thenReturn(mockedErgebnismeldungDTO);

                unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria);

                Mockito.verify(statusClient).postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), mockedNow);
            }
        }

        @Nested
        class ForSchnellmeldung {

            @Test
            void should_sendSendungsuhrzeiten_when_validErgebnisse() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 1L;
                val meldungsart = MeldungsartModel.V1;
                val hauptwahlbezirkID = "hauptwahlbezirkID";
                val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart,
                        hauptwahlbezirkID);

                val mockedWahlbezirkartOfCurrentUser = WahlbezirkArtModel.UWB;
                val mockedWahlartOfCurrentWahltag = WahlartModel.BTW;
                val mockedEAIMeldungsart = ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG;
                val mockedErgebnismeldungDTO = new ErgebnismeldungDTO().wahlID(wahlID).wahlbezirkID(wahlbezirkID).meldungsart(mockedEAIMeldungsart);
                val mockedNow = LocalDateTime.now();

                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedWahlbezirkartOfCurrentUser);
                Mockito.when(ergebnismeldungValidator.checkValidation(eq(mockedWahlartOfCurrentWahltag), eq(mockedWahlbezirkartOfCurrentUser), eq(wahlbezirkID),
                        eq(wahlID),
                        eq(waehlerverzeichnisNummer), eq(meldungsart))).thenReturn(true);
                Mockito.when(wahlenClient.getWahlartOfCurrentWahltag(wahlID)).thenReturn(mockedWahlartOfCurrentWahltag);
                Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);
                Mockito.when(mapping.toDTO(meldungsart)).thenReturn(mockedEAIMeldungsart);
                Mockito.when(ergebnismeldungMappingService.createErgebnismeldung(eq(mockedWahlartOfCurrentWahltag), eq(wahlID), eq(wahlbezirkID),
                        eq(waehlerverzeichnisNummer), eq(mockedEAIMeldungsart), eq(hauptwahlbezirkID))).thenReturn(mockedErgebnismeldungDTO);

                unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria);

                Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), mockedNow);
            }
        }

        @Test
        void should_callErgebnisValidator_when_called() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 1L;
            val meldungsart = MeldungsartModel.V1;
            val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart, wahlbezirkID);

            val mockedWahlbezirkartOfCurrentUser = WahlbezirkArtModel.UWB;
            val mockedWahlartOfCurrentWahltag = WahlartModel.BTW;

            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedWahlbezirkartOfCurrentUser);
            Mockito.when(wahlenClient.getWahlartOfCurrentWahltag(wahlID)).thenReturn(mockedWahlartOfCurrentWahltag);
            Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);
            Mockito.when(mapping.toDTO(meldungsart)).thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);

            unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria);

            Mockito.verify(ergebnismeldungValidator).validErgebnisseToSendCriteriaOrThrow(ergebnisseToSendCriteria);
            Mockito.verify(ergebnismeldungValidator)
                    .checkValidation(eq(mockedWahlartOfCurrentWahltag), eq(mockedWahlbezirkartOfCurrentUser), eq(wahlbezirkID), eq(wahlID),
                            eq(waehlerverzeichnisNummer), eq(meldungsart));
        }

        @Test
        void should_sendErgebnisse_when_ergebnisseAreValid() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 1L;
            val meldungsart = MeldungsartModel.V1;
            val hauptwahlbezirkID = "hauptwahlbezirkID";
            val ergebnisseToSendCriteria = new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlerverzeichnisNummer, meldungsart, hauptwahlbezirkID);

            val mockedWahlbezirkartOfCurrentUser = WahlbezirkArtModel.UWB;
            val mockedWahlartOfCurrentWahltag = WahlartModel.BTW;
            val mockedErgebnismeldungDTO = new ErgebnismeldungDTO().wahlbezirkID(wahlbezirkID);
            val mockedEAIMeldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;

            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedWahlbezirkartOfCurrentUser);
            Mockito.when(ergebnismeldungValidator.checkValidation(eq(mockedWahlartOfCurrentWahltag), eq(mockedWahlbezirkartOfCurrentUser), eq(wahlbezirkID),
                    eq(wahlID),
                    eq(waehlerverzeichnisNummer), eq(meldungsart))).thenReturn(true);
            Mockito.when(wahlenClient.getWahlartOfCurrentWahltag(wahlID)).thenReturn(mockedWahlartOfCurrentWahltag);
            Mockito.when(urnenwahlClient.isWahlbezirkGeschlossen(wahlbezirkID)).thenReturn(true);
            Mockito.when(mapping.toDTO(meldungsart)).thenReturn(mockedEAIMeldungsart);
            Mockito.when(ergebnismeldungMappingService.createErgebnismeldung(eq(mockedWahlartOfCurrentWahltag), eq(wahlID), eq(wahlbezirkID),
                    eq(waehlerverzeichnisNummer), eq(mockedEAIMeldungsart), eq(hauptwahlbezirkID))).thenReturn(mockedErgebnismeldungDTO);

            unitUnderTest.sendErgebnisse(ergebnisseToSendCriteria);

            Mockito.verify(eaiService).sendErgebnismeldung(mockedErgebnismeldungDTO);
        }
    }

    private MeldungModel createMeldungWithValidierungsstatus(final ValidierungsstatusModel validierungsstatus) {
        return new MeldungModel(validierungsstatus, true, true, LocalDateTime.now());
    }

    private StimmzettelumschlaegeModel createStimmzettelumschlaegeWithUrnenOeffnungsUhrzeit(final LocalDateTime urnenOeffnungUhrszeit) {
        return new StimmzettelumschlaegeModel(null, urnenOeffnungUhrszeit, 1, 2);
    }
}
