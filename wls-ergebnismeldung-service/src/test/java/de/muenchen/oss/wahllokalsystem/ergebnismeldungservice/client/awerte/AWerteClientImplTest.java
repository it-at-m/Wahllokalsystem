package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.awerte;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
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
class AWerteClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenControllerApi wahldatenControllerApi;

    @Mock
    AWerteClientMapper aWerteClientMapper;

    @InjectMocks
    AWerteClientImpl unitUnderTest;

    @Nested
    class GetAWerte {

        @Test
        void should_return_mappedAWerteList_when_clientReturnsWahlberechtigteDTOList() {
            String wahlbezirkID = "wahlbezirkID";
            val mockedClientResponse = createListOfWahlberechtigteDTO();
            val mockedMappedClientResponse = List.of(AWerteModel.builder().build());

            Mockito.when(wahldatenControllerApi.loadWahlberechtigte(any()))
                    .thenReturn(mockedClientResponse);
            Mockito.when(aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(mockedClientResponse))
                    .thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.getAWerte(wahlbezirkID);
            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void should_throwTechnischeWlsException_whenClientThrowsAnyException() {
            val mockedWlsException = TechnischeWlsException.withCode("100")
                    .buildWithMessage("Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

            Mockito.when(wahldatenControllerApi.loadWahlberechtigte(any()))
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getAWerte("wahlbezirkId")).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_whenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND).buildWithMessage("not found");

            Mockito.when(wahldatenControllerApi.loadWahlberechtigte("wahlbezirkId")).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getAWerte("wahlbezirkId")).isSameAs(mockedWlsException);
        }
    }

    private List<WahlberechtigteDTO> createListOfWahlberechtigteDTO() {
        val wb1 = new WahlberechtigteDTO();
        wb1.setWahlbezirkID("wahlbezirkID1");
        wb1.setWahlID("wahlID");
        wb1.setA1(2L);
        wb1.setA2(3L);
        wb1.setA3(5L);
        val wb2 = new WahlberechtigteDTO();
        wb2.setWahlbezirkID("wahlbezirkID2");
        wb2.setWahlID("wahlID");
        wb2.setA1(3L);
        wb2.setA2(4L);
        wb2.setA3(7L);
        val wb3 = new WahlberechtigteDTO();
        wb3.setWahlbezirkID("wahlbezirkID3");
        wb3.setWahlID("wahlID");
        wb3.setA1(4L);
        wb3.setA2(5L);
        wb3.setA3(9L);
        return List.of(wb1, wb2, wb3);
    }
}
