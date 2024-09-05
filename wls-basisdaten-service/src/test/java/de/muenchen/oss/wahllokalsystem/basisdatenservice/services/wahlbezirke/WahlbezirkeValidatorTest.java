package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

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
class WahlbezirkeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlbezirkeValidator unitUnderTest;

    @Nested
    class ValidWahltagIDParamOrThrow {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenRequestParamIsValid() {
            val requestParam = "wahltagID";
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahltagIDParamOrThrow(requestParam));
        }

        @Test
        void exceptionWhenWahlIDisNullOrEmptyOrBlank() {
            val requestParam2 = "";
            val requestParam3 = "     ";
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahltagIDParamOrThrow(null)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahltagIDParamOrThrow(requestParam2)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahltagIDParamOrThrow(requestParam3)).isSameAs(mockedWlsException);
        }
    }
}
