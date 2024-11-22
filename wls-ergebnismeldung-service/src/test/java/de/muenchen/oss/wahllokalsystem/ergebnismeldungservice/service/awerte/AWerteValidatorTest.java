package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
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
class AWerteValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    AWerteValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDParamOrThrow {

        @Test
        void noExceptionWhenCriteriaIsValid() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDParamOrThrow("validWahlbezirkID"));
        }

        @Test
        void exceptionWhenCriteriaIsNull() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETAWERTE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlbezirkIDParamOrThrow(null)).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenCriteriaIsEmpty() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETAWERTE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlbezirkIDParamOrThrow("")).isSameAs(mockedException);
        }

        @Test
        void exceptionWhenCriteriaIsBlank() {
            val mockedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETAWERTE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlbezirkIDParamOrThrow("   ")).isSameAs(mockedException);
        }
    }
}
