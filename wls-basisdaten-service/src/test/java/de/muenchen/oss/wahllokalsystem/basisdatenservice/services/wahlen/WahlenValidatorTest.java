package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
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
class WahlenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlenValidator unitUnderTest;

    @Nested
    class ValidWahlenCriteriaOrThrow {

        @Test
        void noExceptionWhenCriteriaIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("validWahltagID"));
        }

        @Test
        void exceptionWhenCriteriaIsNull() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenCriteriaIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("")).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenCriteriaIsBlank() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLEN_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlenCriteriaOrThrow("   ")).isSameAs(mockedException);
        }
    }

}
