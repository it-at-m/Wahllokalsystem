package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
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
class KonfigurierterWahltagClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Mock
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @InjectMocks
    KonfigurierterWahltagClientImpl unitUnderTest;

    @Nested
    class LoadBasisdaten {

        @Test
        void clientResponseIsMapped() {
            val mockedClientResponse = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1),
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            val mockedMappedClientResponse = KonfigurierterWahltagModel.builder().build();

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag())
                    .thenReturn(mockedClientResponse);
            Mockito.when(konfigurierterWahltagClientMapper.fromRemoteClientDTOToModel(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.getKonfigurierterWahltag();

            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKonfigurierterWahltag()).isSameAs(mockedWlsException);
        }

        @Test
        void controllerApiExceptionIsMapped() {
            val mockedException = TechnischeWlsException.withCode("100")
                    .buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag())
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_SERVICE)).thenThrow(mockedException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKonfigurierterWahltag()).isSameAs(mockedException);
        }
    }
}
