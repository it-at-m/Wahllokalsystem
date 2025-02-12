package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahldatenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.client.WahlergebnisControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
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
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class EAIClientImplTest {

    @Mock
    WahldatenControllerApi wahldatenControllerApi;
    @Mock
    WahlergebnisControllerApi wahlergebnisControllerApi;

    @Mock
    AWerteClientMapper aWerteClientMapper;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    EAIClientImpl unitUnderTest;

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
        void should_returnNull_when_clientThrowsAnyException() {
            Mockito.when(wahldatenControllerApi.loadWahlberechtigte(any()))
                    .thenThrow(new RestClientException("error occurs while attempting to invoke the API"));

            Assertions.assertThat(unitUnderTest.getAWerte("wahlbezirkId")).isNull();
        }
    }

    @Nested
    class SendErgebnismeldung {

        @Test
        void should_callApiController_when_ergebnismeldungIsGiven() {
            val ergebnismeldungDTO = new ErgebnismeldungDTO();

            unitUnderTest.sendErgebnismeldung(ergebnismeldungDTO);

            Mockito.verify(wahlergebnisControllerApi).saveErgebnismeldung(ergebnismeldungDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_apiThrewException() {
            val ergebnismeldungDTO = new ErgebnismeldungDTO().meldungsart(ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG);

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlergebnisControllerApi).saveErgebnismeldung(ergebnismeldungDTO);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.sendErgebnismeldung(ergebnismeldungDTO)).isSameAs(mockedWlsException);
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
