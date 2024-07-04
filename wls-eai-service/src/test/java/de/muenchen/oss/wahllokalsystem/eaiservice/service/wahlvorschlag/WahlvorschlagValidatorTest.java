package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.UUID;
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
class WahlvorschlagValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorschlagValidator unitUnderTest;

    @Nested
    class ValidateWahlbezirkIDOrThrow {

        @Test
        void noExceptionWhenWahlbezirkIDIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNUll() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmptyString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsBlankString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }

    @Nested
    class ValidateWahlIDOrThrow {

        @Test
        void noExceptionWhenWahlIDIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void exceptionWhenWahlIDIsNUll() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsEmptyString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsBlankString() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }

}
