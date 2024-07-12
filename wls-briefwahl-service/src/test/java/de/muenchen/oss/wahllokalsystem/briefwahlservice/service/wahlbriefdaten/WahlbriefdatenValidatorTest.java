package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class WahlbriefdatenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlbriefdatenValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDOrThrow {

        @Test
        void noExceptionOnValidWahlbezirkID() {
            val validWahlbezirkID = "wahlbezirkID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(validWahlbezirkID));
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("")).isSameAs(mockedException);
        }

    }

    @Nested
    class ValidWahlbriefdatenToSetOrThrow {

        @Test
        void noExceptionWhenModelIsValid() {
            val validModel = new WahlbriefdatenModel("wahlbezirkID", null, null, null, null, null);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbriefdatenToSetOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbriefdatenToSetOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsNull() {
            val invalidModel = new WahlbriefdatenModel(null, null, null, null, null, null);

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbriefdatenToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsEmpty() {
            val invalidModel = new WahlbriefdatenModel("", null, null, null, null, null);

            val mockedException = FachlicheWlsException.withCode("000").inService("service").buildWithMessage("message");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbriefdatenToSetOrThrow(invalidModel)).isSameAs(mockedException);
        }
    }

}
