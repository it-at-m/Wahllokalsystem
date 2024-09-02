package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static org.mockito.ArgumentMatchers.any;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.Set;
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
class WahlbezirkeClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenControllerApi wahldatenControllerApi;

    @Mock
    WahlbezirkeClientMapper wahlbezirkeClientMapper;

    @InjectMocks
    WahlbezirkeClientImpl unitUnderTest;

    @Nested
    class LoadWahlbezirke {

        @Test
        void clientResponseIsMapped() {
            val testDate = LocalDate.now().minusMonths(3);

            val mockedClientResponse = MockDataFactory.createSetOfClientWahlbezirkDTO(testDate);
            val mockedMappedClientResponse = Set.of(WahlbezirkModel.builder().build());

            Mockito.when(wahldatenControllerApi.loadWahlbezirke(testDate, "wahltagNummer"))
                    .thenReturn(mockedClientResponse);
            Mockito.when(wahlbezirkeClientMapper.fromRemoteSetOfDTOsToSetOfModels(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.loadWahlbezirke(testDate, "wahltagNummer");

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahldatenControllerApi.loadWahlbezirke(any(), any())).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadWahlbezirke(LocalDate.now(), "wahltagNummer")).isSameAs(mockedWlsException);
        }

        @Test
        void controllerApiExceptionIsMapped() {
            val testDate = LocalDate.now().minusMonths(3);
            val mockedException = TechnischeWlsException.withCode("100")
                    .buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(wahldatenControllerApi.loadWahlbezirke(any(), any()))
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenThrow(mockedException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadWahlbezirke(testDate, "wahltagNummer")).isSameAs(mockedException);
        }
    }
}
