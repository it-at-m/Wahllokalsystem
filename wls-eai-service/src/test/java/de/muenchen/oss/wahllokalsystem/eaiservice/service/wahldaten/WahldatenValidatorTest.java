package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
class WahldatenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahldatenValidator unitUnderTest;

    @Nested
    class ValidGetWahltageParameterOrThrow {

        @Test
        void noExceptionWhenWahltagIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validGetWahltageParameterOrThrow(LocalDate.now()));
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLTAGE_TAG_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validGetWahltageParameterOrThrow(null)).isSameAs(mockedWlsException);
        }

    }

    @Nested
    class ValidGetWahlenParameterOrThrow {

        @Test
        void noExceptionWhenGetWahlenParameterAreValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validGetWahlenParameterOrThrow(LocalDate.now(), ""));
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEN_WAHLTAG_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlenParameterOrThrow(null, "")).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenNummerIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEN_NUMMER_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlenParameterOrThrow(LocalDate.now(), null)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ValidGetWahlbezirkeParameterOrThrow {

        @Test
        void noExceptionWhenGetWahlbezirkeParameterAreValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validGetWahlbezirkeParameterOrThrow(LocalDate.now(), ""));
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBEZIRKE_WAHLTAG_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlbezirkeParameterOrThrow(null, "")).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenNummerIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBEZIRKE_NUMMER_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlbezirkeParameterOrThrow(LocalDate.now(), null))
                    .isSameAs(mockedWlsException);
        }

    }

    @Nested
    class ValidGetWahlberechtigteParameterOrThrow {

        @Test
        void noExceptionWhenGetWahlberechtigteParameterAreValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validGetWahlberechtigteParameterOrThrow("wahlbezirkID"));
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlberechtigteParameterOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmptyString() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlberechtigteParameterOrThrow("")).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsBlank() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetWahlberechtigteParameterOrThrow("  ")).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ValidGetBasisdatenParameterOrThrow {

        @Test
        void noExceptinWhenParametersAreValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validGetBasisdatenParameterOrThrow(LocalDate.now(), "nummer"));
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADBASISDATEN_TAG_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetBasisdatenParameterOrThrow(null, "nummer")).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenNummerIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADBASISDATEN_NUMMER_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validGetBasisdatenParameterOrThrow(LocalDate.now(), null))
                    .isSameAs(mockedWlsException);
        }
    }

}
