package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahltermindatenControllerApi;
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
class WahltermindatenClientImplTest {

    @Mock
    WahltermindatenControllerApi wahltermindatenControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahltermindatenClientImpl unitUnderTest;

    @Nested
    class PutWahltermindaten {

        @Test
        void should_verifyPutWahltermindatenApiCall_when_wahltagIDIsGiven() {
            val wahltagID = "wahltagID";

            unitUnderTest.putWahltermindaten(wahltagID);

            Mockito.verify(wahltermindatenControllerApi).putWahltermindaten(wahltagID);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(mockedWlsException).when(wahltermindatenControllerApi).putWahltermindaten(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahltermindatenControllerApi).putWahltermindaten(wahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class DeleteWahltermindaten {
        @Test
        void should_verifyDeleteWahltermindatenAPICall_when_wahltagIDIsGiven() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteWahltermindaten(wahltagID);

            Mockito.verify(wahltermindatenControllerApi).deleteWahltermindaten(wahltagID);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(mockedWlsException).when(wahltermindatenControllerApi).deleteWahltermindaten(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahltermindatenControllerApi).deleteWahltermindaten(wahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isSameAs(mockedWlsException);
        }

    }
}
