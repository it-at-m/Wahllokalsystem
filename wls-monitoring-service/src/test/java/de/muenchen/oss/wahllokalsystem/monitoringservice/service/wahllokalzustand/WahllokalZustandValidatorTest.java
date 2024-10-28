package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandValidatorTest {

    private final FachlicheWlsException mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahllokalZustandValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDOrThrow {
        @Test
        void should_notThrowAnyException_when_wahlbezirkIdIsNotNullOrBlankOrEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("wahlbezirkID", operation));
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                    case POST_LASTSEEN -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG))
                            .thenReturn(mockedFachlicheWlsException);
                    case POST_LETZTEABMELDUNG -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG))
                            .thenReturn(mockedFachlicheWlsException);
                    default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                            .thenReturn(mockedFachlicheWlsException);
                }
                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(null, operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_LASTSEEN -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG))
                        .thenReturn(mockedFachlicheWlsException);
                case POST_LETZTEABMELDUNG -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG))
                        .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }
                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("   ", operation)).isSameAs(mockedFachlicheWlsException);
            }

        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_LASTSEEN -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG))
                        .thenReturn(mockedFachlicheWlsException);
                case POST_LETZTEABMELDUNG -> Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG))
                        .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }
                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("", operation)).isSameAs(mockedFachlicheWlsException);
            }

        }
    }

    @Nested
    class ValidWahlIdUndWahlbezirkIDOrThrow {
        @Test
        void should_notThrowAnyException_when_wahlIdAndWahlbezirkIdAreNotNullOrBlankOrEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), operation));
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_paramBezirkUndWahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                    case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                            .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(null, operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                    case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                    .thenReturn(mockedFachlicheWlsException);
                    default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                            .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID(null, "wahlbezirkID01"), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("   ", "wahlbezirkID01"), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("", "wahlbezirkID01"), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", null), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", "   "), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", ""), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }
    }

    @Nested
    class ValidSendungsdatenModel {
        @Test
        void should_notThrowAnyException_when_wahlIdAndWahlbezirkIdAreNotNullOrBlankOrEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                if (operation == WahllokalZustandOperation.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT || operation == WahllokalZustandOperation.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT){
                    Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "wahlbezirkID01")).build(), operation));
                }
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_paramSendungsdatenIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(null, operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_paramBezirkUndWahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(null).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID(null, "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("  ", "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("", "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", null)).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "   ")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validSendungsdatenModel(SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }
    }

    @Nested
    class ValidDruckdatenModel {
        @Test
        void should_notThrowAnyException_when_wahlIdAndWahlbezirkIdAreNotNullOrBlankOrEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                if (operation == WahllokalZustandOperation.POST_SCHNELLMELDUNG_DRUCKUHRZEIT || operation == WahllokalZustandOperation.POST_NIEDERSCHRIFT_DRUCKUHRZEIT){
                    Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "wahlbezirkID01")).build(), operation));
                }
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_paramSendungsdatenIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(null, operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_paramBezirkUndWahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(null).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID(null, "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("  ", "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("", "wahlbezirkID01")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsNull() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", null)).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsBlank() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "   ")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }

        @Test
        void should_throwFachlicheWlsException_when_wahlbezirkIdIsEmpty() {
            for(WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                switch (operation) {
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                        Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG))
                                .thenReturn(mockedFachlicheWlsException);
                default -> Mockito.when(exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG)))
                        .thenReturn(mockedFachlicheWlsException);
                }

                Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validDruckdatenModel(DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID01", "")).build(), operation)).isSameAs(mockedFachlicheWlsException);
            }
        }
    }
}