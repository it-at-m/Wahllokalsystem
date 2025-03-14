package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
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
        void should_notThrowException_when_wahlbezirkIDIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlbezirkIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }

    @Nested
    class ValidateWahlIDOrThrow {

        @Test
        void should_notThrowException_when_wahlIDIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow(UUID.randomUUID().toString()));
        }

        @Test
        void should_throwWlsException_when_wahlIDIsNull() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlIDIsEmpty() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow("")).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlIDIsBlank() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateWahlIDOrThrow("   ")).isSameAs(mockedFachlicheWlsException);
        }
    }
}
