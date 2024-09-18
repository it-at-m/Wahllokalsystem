package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.List;
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
class WahlenClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenControllerApi wahldatenControllerApi;

    @Mock
    WahlenClientMapper wahlenClientMapper;

    @InjectMocks
    WahlenClientImpl unitUnderTest;

    @Nested
    class GetWahlen {

        @Test
        void clientResponseIsMapped() {
            val testDate = LocalDate.now();

            val mockedClientResponse = createClientWahlenDTO();
            val mockedMappedClientResponse = List.of(WahlModel.builder().build());

            Mockito.when(wahldatenControllerApi.loadWahlen(testDate, "1"))
                    .thenReturn(mockedClientResponse);
            Mockito.when(wahlenClientMapper.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.getWahlen(new WahltagWithNummer(testDate, "1"));

            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahldatenControllerApi.loadWahlen(any(), any())).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlen(new WahltagWithNummer(LocalDate.now(), "1")))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void controllerApiExceptionIsMapped() {
            val testDate = LocalDate.now();
            val mockedException = TechnischeWlsException.withCode("100")
                    .buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(wahldatenControllerApi.loadWahlen(any(), any()))
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenThrow(mockedException);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlen(new WahltagWithNummer(testDate, "1"))).isSameAs(mockedException);
        }

        private Set<WahlDTO> createClientWahlenDTO() {
            val wahl1 = new WahlDTO();
            wahl1.setIdentifikator("identifikatorWahl1");
            wahl1.setNummer("nummerWahl1");
            wahl1.setName("nameWahl1");
            wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl1.setWahltag(LocalDate.now());

            val wahl2 = new WahlDTO();
            wahl2.setIdentifikator("identifikatorWahl2");
            wahl2.setNummer("nummerWahl2");
            wahl2.setName("nameWahl2");
            wahl2.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl2.setWahltag(LocalDate.now());

            val wahl3 = new WahlDTO();
            wahl3.setIdentifikator("identifikatorWahl3");
            wahl3.setNummer("nummerWahl3");
            wahl3.setName("nameWahl3");
            wahl3.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl3.setWahltag(LocalDate.now());

            return Set.of(wahl1, wahl2, wahl3);
        }
    }
}
