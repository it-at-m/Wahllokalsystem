package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class WaehleranzahlControllerTest {
    @Mock
    WaehleranzahlService waehleranzahlService;

    @Mock
    WaehleranzahlDTOMapper waehleranzahlDTOMapper;

    @InjectMocks
    WaehleranzahlController unitUnderTest;

    @Nested
    class GetWahlbeteiligung {

        @Test
        void should_returnHttp204AndBodyIsNull_when_serviceIsCalledWithNoData() {
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            Mockito.when(waehleranzahlService.getWahlbeteiligung(bezirkUndWahlID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlbeteiligung(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

    @Nested
    class PostWahlbeteiligung {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val requestBody = Mockito.mock(WaehleranzahlDTO.class);
            val mockedMappedRequest = Mockito.mock(WaehleranzahlModel.class);

            Mockito.when(waehleranzahlDTOMapper.toSetModel(bezirkUndWahlID, requestBody)).thenReturn(mockedMappedRequest);

            unitUnderTest.postWahlbeteiligung(wahlbezirkID, wahlID, requestBody);

            Mockito.verify(waehleranzahlService).postWahlbeteiligung(mockedMappedRequest);
        }
    }
}
