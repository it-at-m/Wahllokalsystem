package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class KopfdatenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    KopfdatenValidator unitUnderTest;

    @Nested
    class ValidWahlIdUndWahlbezirkIDOrThrow {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenRequestParamIsValid() {
            val requestParam = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam));
        }

        @Test
        void exceptionWhenWahlIDisNullOrEmptyOrBlank() {
            val requestParam1 = new BezirkUndWahlID(null, "wahlbezirkID");
            val requestParam2 = new BezirkUndWahlID("", "wahlbezirkID");
            val requestParam3 = new BezirkUndWahlID("     ", "wahlbezirkID");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam1)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam2)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam3)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDisNullOrEmptyOrBlank() {
            val requestParam1 = new BezirkUndWahlID("wahlID", null);
            val requestParam2 = new BezirkUndWahlID("wahlID", "");
            val requestParam3 = new BezirkUndWahlID("wahlID", "       ");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam1)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam2)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(requestParam3)).isSameAs(mockedWlsException);
        }
    }
}
