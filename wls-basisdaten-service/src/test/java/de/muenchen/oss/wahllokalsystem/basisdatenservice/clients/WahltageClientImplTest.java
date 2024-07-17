package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static org.mockito.ArgumentMatchers.any;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class WahltageClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenControllerApi wahldatenControllerApi;

    @Mock
    WahltageClientMapper wahltageClientMapper;

    @InjectMocks
    WahltageClientImpl unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void clientResponseIsMapped() {
            val testDate = LocalDate.now().minusMonths(3);

            val mockedClientResponse = new WahltageDTO();
            val mockedMappedClientResponse = List.of(WahltagModel.builder().build());

            Mockito.when(wahldatenControllerApi.loadWahltageSinceIncluding(testDate))
                    .thenReturn(mockedClientResponse);
            Mockito.when(wahltageClientMapper.fromRemoteClientWahltageDTOtoListOfWahltagModel(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.getWahltage(testDate);

            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahldatenControllerApi.loadWahltageSinceIncluding(any())).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltage(LocalDate.now())).isSameAs(mockedWlsException);
        }

        @Test
        void controllerApiExceptionIsMapped(){
            val testDate = LocalDate.now().minusMonths(3);
            val mockedException = TechnischeWlsException.withCode("100").buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(wahldatenControllerApi.loadWahltageSinceIncluding(any())).thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenThrow(mockedException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltage(testDate)).isSameAs(mockedException);
        }
    }
}
