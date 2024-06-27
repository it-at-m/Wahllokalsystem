package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
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
class WahlvorschlaegeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorschlaegeValidator unitUnderTest;

    @Nested
    class ValidWahlIdUndWahlbezirkIDOrThrow {

        private final FachlicheWlsException mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID", "wahlbezirkID")));
        }

        @Test
        void exceptionWhenParameterIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedFachlicheWlsException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedFachlicheWlsException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID(null, "wahlbezirkID")))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsEmptyString() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedFachlicheWlsException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("", "wahlbezirkID")))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedFachlicheWlsException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID", null)))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmptyString() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG)).thenReturn(mockedFachlicheWlsException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID", "")))
                    .isSameAs(mockedFachlicheWlsException);
        }
    }

}
