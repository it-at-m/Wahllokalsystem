package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.client.KonfigurationControllerApi;
import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.model.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.authservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.authservice.security.LegalLoginInterval;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private static final String KONFIG_KEY_EARLIEST_LOGIN = "KONFIG_KEY_EARLIEST_LOGIN";
    private static final String KONFIG_KEY_LATEST_LOGIN = "KONFIG_KEY_LATEST_LOGIN";
    private static final String KONFIG_DATETIME_FORMAT = "dd.MM.yyyy HH:mm";

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
        unitUnderTest.konfigKeyFruehesterLogin = KONFIG_KEY_EARLIEST_LOGIN;
        unitUnderTest.konfigKeySpaetesterLogin = KONFIG_KEY_LATEST_LOGIN;
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

    @Nested
    class GetLegalLoginInterval {

        @Test
        void should_returnInterval_when_apiReturnValues() {
            val earliestLoginDatetime = "22.11.2024 10:23";
            val latestLoginDatetime = "22.11.2024 13:42";

            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(KONFIG_KEY_EARLIEST_LOGIN))
                    .thenReturn(new KonfigurationDTO().wert(earliestLoginDatetime));
            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(KONFIG_KEY_LATEST_LOGIN))
                    .thenReturn(new KonfigurationDTO().wert(latestLoginDatetime));

            val result = unitUnderTest.getLegalLoginInterval();

            val expectedResult = new LegalLoginInterval(LocalDateTime.parse(earliestLoginDatetime, DateTimeFormatter.ofPattern(KONFIG_DATETIME_FORMAT)),
                    LocalDateTime.parse(latestLoginDatetime, DateTimeFormatter.ofPattern(KONFIG_DATETIME_FORMAT)));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_throwDateTimeException_when_dateTimeStringIsInWrongFormat() {
            val earliestLoginDatetimeInWrongFormat = "2024-11-22T10:23:00.000";
            val latestLoginDatetime = "22.11.2024 13:42";

            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(KONFIG_KEY_EARLIEST_LOGIN))
                    .thenReturn(new KonfigurationDTO().wert(earliestLoginDatetimeInWrongFormat));
            Mockito.when(konfigurationControllerApi.getKonfigurationUnauthorized(KONFIG_KEY_LATEST_LOGIN))
                    .thenReturn(new KonfigurationDTO().wert(latestLoginDatetime));

            Assertions.assertThatThrownBy(() -> unitUnderTest.getLegalLoginInterval())
                    .isInstanceOf(DateTimeException.class)
                    .hasMessageContaining(earliestLoginDatetimeInWrongFormat);
        }

        @Test
        void should_mapToTechnischeWlsException_when_nonWlsExceptionIsThrownFromAPI() {
            val mockedTechnischeWlsException = TechnischeWlsException.withCode("").buildWithMessage("konfiguration controller api call failed");

            Mockito.doThrow(new RuntimeException("konfiguration controller api call failed")).when(konfigurationControllerApi)
                    .getKonfigurationUnauthorized(KONFIG_KEY_EARLIEST_LOGIN);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_KONFIGSERVICE))
                    .thenReturn(mockedTechnischeWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getLegalLoginInterval())
                    .isSameAs(mockedTechnischeWlsException);
        }

        @Test
        void should_reThrowWlsException_when_wlsExceptionisThrownFromAPI() {
            val wlsException = TechnischeWlsException.withCode("").buildWithMessage("konfiguration controller api call failed");

            Mockito.doThrow(wlsException).when(konfigurationControllerApi)
                    .getKonfigurationUnauthorized(KONFIG_KEY_EARLIEST_LOGIN);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getLegalLoginInterval())
                    .isSameAs(wlsException);
        }

    }

}
