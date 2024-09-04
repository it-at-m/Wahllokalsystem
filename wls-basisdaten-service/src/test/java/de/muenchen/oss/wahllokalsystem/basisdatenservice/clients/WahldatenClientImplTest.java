package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class WahldatenClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenControllerApi wahldatenControllerApi;

    @Mock
    WahldatenClientMapper wahldatenClientMapper;

    @InjectMocks
    WahldatenClientImpl unitUnderTest;

    @Nested
    class LoadBasisdaten {

        @Test
        void clientResponseIsMapped() {
            val testDate = LocalDate.now();

            val mockedClientResponse = MockDataFactory.createClientBasisdatenDTO(LocalDate.now());
            val mockedMappedClientResponse = BasisdatenModel.builder().build();

            Mockito.when(wahldatenControllerApi.loadBasisdaten(testDate, "0"))
                    .thenReturn(mockedClientResponse);
            Mockito.when(wahldatenClientMapper.fromRemoteClientDTOToModel(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(testDate, "0"));

            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahldatenControllerApi.loadBasisdaten(any(), any())).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_NO_BASISDATEN)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0")))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void controllerApiExceptionIsMapped() {
            val testDate = LocalDate.now();
            val mockedException = TechnischeWlsException.withCode("100")
                    .buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(wahldatenControllerApi.loadBasisdaten(any(), any()))
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenThrow(mockedException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadBasisdaten(new WahltagWithNummer(testDate, "0"))).isSameAs(mockedException);
        }

    }
}
