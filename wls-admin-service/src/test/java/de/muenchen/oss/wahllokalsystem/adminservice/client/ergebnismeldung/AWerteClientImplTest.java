package de.muenchen.oss.wahllokalsystem.adminservice.client.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.ergebnismeldung.client.AWerteControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
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
class AWerteClientImplTest {

    @Mock
    AWerteControllerApi awerteControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    AWerteClientImpl unitUnderTest;

    @Nested
    class InitialiseAWerte {

        @Test
        void should_verifyInitialiseAWerteApiCall_when_wahltagIDIsGiven() {
            val wahlbezirkIDList = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");

            unitUnderTest.initialiseAWerte(wahlbezirkIDList);

            Mockito.verify(awerteControllerApi).initialiseAWerte(wahlbezirkIDList);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromAWerteApi() {
            val wahlbezirkIDList = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with AWerte api failed");

            Mockito.doThrow(mockedWlsException).when(awerteControllerApi).initialiseAWerte(wahlbezirkIDList);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.initialiseAWerte(wahlbezirkIDList)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromAWerteApi() {
            val wahlbezirkIDList = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with AWerte api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(awerteControllerApi).initialiseAWerte(wahlbezirkIDList);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_ERGEBNISMELDUNG))
                .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.initialiseAWerte(wahlbezirkIDList)).isSameAs(mockedWlsException);
        }
    }
}
