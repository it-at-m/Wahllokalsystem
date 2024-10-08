package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

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
class WahltermindatenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahltermindatenValidator unitUnderTest;

    @Nested
    class ValidateParameterToInitWahltermindaten {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

        @Test
        void should_throwNoException_when_wahltagIDIsValid() {
            val wahltagID = "wahltagID";

            unitUnderTest.validateParameterToInitWahltermindaten(wahltagID);
        }

        @Test
        void should_throwException_when_wahltagIDIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToInitWahltermindaten(null)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwException_when_wahltagIDIsEmpty() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToInitWahltermindaten("")).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwException_when_wahltagIDIsBlank() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToInitWahltermindaten("   ")).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ValidateParameterToDeleteWahltermindaten {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

        @Test
        void should_throwNoException_when_wahltagIDIsValid() {
            val wahltagID = "wahltagID";

            unitUnderTest.validateParameterToDeleteWahltermindaten(wahltagID);
        }

        @Test
        void should_throwException_when_wahltagIDIsNull() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToDeleteWahltermindaten(null)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwException_when_wahltagIDIsEmpty() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToDeleteWahltermindaten("")).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwException_when_wahltagIDIsBlank() {
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validateParameterToDeleteWahltermindaten("   ")).isSameAs(mockedWlsException);
        }
    }

}
