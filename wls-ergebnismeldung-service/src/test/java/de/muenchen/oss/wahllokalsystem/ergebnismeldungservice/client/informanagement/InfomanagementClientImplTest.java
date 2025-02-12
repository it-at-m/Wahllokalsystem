package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.informanagement;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
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
class InfomanagementClientImplTest {

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    InfomanagementClientImpl unitUnderTest;

    @Nested
    class GetWahltagID {

        @Test
        void should_returnWahltagID_when_apiReturnedWahltagWithID() {
            val mockedWahltagID = "wahltagID";
            val mockedAPiResponse = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedAPiResponse);

            val result = unitUnderTest.getWahltagID();

            Assertions.assertThat(result).isEqualTo(mockedWahltagID);
        }

        @Test
        void should_rethrowWlsException_when_apiThrewWlsException() {
            val mockedApiWlsException = TechnischeWlsException.withCode("000").buildWithMessage("wls exception from api");
            Mockito.doThrow(mockedApiWlsException).when(konfigurierterWahltagControllerApi).getKonfigurierterWahltag();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltagID()).isSameAs(mockedApiWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrewNonWlsException() {
            val mockedApiException = new RuntimeException("api call failed");
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("transformed api exception");

            Mockito.doThrow(mockedApiException).when(konfigurierterWahltagControllerApi).getKonfigurierterWahltag();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltagID()).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_when_apiReturnedNull() {
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("transformed api exception");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltagID()).isSameAs(mockedWlsException);

        }

        @Test
        void should_throwFachlicheWlsException_when_apiReturnedWahltagWithoutID() {
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("transformed api exception");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(new KonfigurierterWahltagDTO().wahltagID(null));
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltagID()).isSameAs(mockedWlsException);
        }
    }

}
