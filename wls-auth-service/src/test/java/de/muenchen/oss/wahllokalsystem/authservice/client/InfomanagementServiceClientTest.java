package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.client.KonfigurationControllerApi;
import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.model.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.authservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InfomanagementServiceClientTest {

    private static final String konfigKeyWelcomeMessage = "konfigKeyWelcomeMessage";

    private static final String defaultWelcomeMessage = "default welcome message";

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurationControllerApi konfigurationControllerApi;

    @InjectMocks
    InfomanagementServiceClient unitUnderTest;

    @BeforeEach
    void setup() {
        unitUnderTest.konfigKeyWelcomeMessage = konfigKeyWelcomeMessage;
        unitUnderTest.defaultWelcomeMessage = defaultWelcomeMessage;
    }

    @Nested
    class GetWelcomeMessage {

        @Test
        void should_returnMessageFromAPI_when_receivingConfigWithValue() {
            val mockedKonfigurationWert = "konfiguration wert";
            val mockedKonfigurationDTO = new KonfigurationDTO().wert(mockedKonfigurationWert);
            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(konfigKeyWelcomeMessage)).thenReturn(mockedKonfigurationDTO);

            val result = unitUnderTest.getWelcomeMessage();

            Assertions.assertThat(result).isEqualTo(mockedKonfigurationWert);
        }

        @Test
        void should_returnDefaultValueFromKonfiguration_when_receivingConfigWithoutValue() {
            val mockedKonfigurationWert = "konfiguration wert";
            val mockedKonfigurationDTO = new KonfigurationDTO().standardwert(mockedKonfigurationWert);
            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(konfigKeyWelcomeMessage)).thenReturn(mockedKonfigurationDTO);

            val result = unitUnderTest.getWelcomeMessage();

            Assertions.assertThat(result).isEqualTo(mockedKonfigurationWert);
        }

        @Test
        void should_returnDefaultMessage_when_receivingNullFromApi() {
            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(konfigKeyWelcomeMessage)).thenReturn(null);

            val result = unitUnderTest.getWelcomeMessage();

            Assertions.assertThat(result).isEqualTo(defaultWelcomeMessage);
        }

        @Test
        void should_returnDefaultMessage_when_theApiThrowsAnException() {
            Mockito.doThrow(new RuntimeException("sth failed")).when(konfigurationControllerApi).getKonfigurationUnauthorized(konfigKeyWelcomeMessage);

            val result = unitUnderTest.getWelcomeMessage();

            Assertions.assertThat(result).isEqualTo(defaultWelcomeMessage);

            Mockito.verify(exceptionFactory).createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_KONFIGSERVICE);
        }
    }

}
