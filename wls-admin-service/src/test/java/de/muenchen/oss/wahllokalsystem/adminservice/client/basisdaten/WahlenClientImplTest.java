package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
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
class WahlenClientImplTest {

    @Mock
    WahlenControllerApi wahlenControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlenClientImpl unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void should_resetWahlen_when_noExceptionIsThrown() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.resetWahlen());

            Mockito.verify(wahlenControllerApi).resetWahlen();
        }

        @Test
        void should_throwTechnischeWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).resetWahlen();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.resetWahlen()).isSameAs(mockedWlsException);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).resetWahlen();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.resetWahlen()).isSameAs(mockedWlsException);
        }
    }
}
