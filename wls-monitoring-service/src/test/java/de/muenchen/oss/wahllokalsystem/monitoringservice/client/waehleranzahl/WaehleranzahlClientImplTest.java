package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahlbeteiligungControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WaehleranzahlClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahlbeteiligungControllerApi wahlbeteiligungControllerApi;

    @Mock
    WaehleranzahlClientMapper waehleranzahlClientMapper;

    @InjectMocks
    WaehleranzahlClientImpl unitUnderTest;

    @Nested
    class PostWahlbeteiligung {

        @Test
        void should_callEaiApiWithDTO_when_clientIsCalledWithModel() {

            val waehleranzahlModel = new WaehleranzahlModel(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 99L, LocalDateTime.now());
            val mockedWahlbeteiligungsMeldungDTO = createWahlbeteiligungsMeldungDTO();
            Mockito.when(waehleranzahlClientMapper.fromModelToRemoteClientDTO(waehleranzahlModel)).thenReturn(mockedWahlbeteiligungsMeldungDTO);

            unitUnderTest.postWahlbeteiligung(waehleranzahlModel);
            Mockito.verify(wahlbeteiligungControllerApi).saveWahlbeteiligung(mockedWahlbeteiligungsMeldungDTO);
        }

        @Test
        void should_throwTechnischeWlsException_when_eaiApiThrowsAnyException() {

            val waehleranzahlModel = new WaehleranzahlModel(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"), 99L, LocalDateTime.now());
            val mockedWahlbeteiligungsMeldungDTO = createWahlbeteiligungsMeldungDTO();
            Mockito.when(waehleranzahlClientMapper.fromModelToRemoteClientDTO(waehleranzahlModel)).thenReturn(mockedWahlbeteiligungsMeldungDTO);

            val mockedWlsException = TechnischeWlsException.withCode("007").buildWithMessage("Dummy-Msg");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI)).thenReturn(mockedWlsException);

            val mockedApiException = new IllegalArgumentException("Nix-Connect");
            Mockito.doThrow(mockedApiException).when(wahlbeteiligungControllerApi).saveWahlbeteiligung(mockedWahlbeteiligungsMeldungDTO);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlbeteiligung(waehleranzahlModel)).isSameAs(mockedWlsException);
        }

        private WahlbeteiligungsMeldungDTO createWahlbeteiligungsMeldungDTO() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val anzahlWahler = 99L;
            val meldeZeitpunkt = OffsetDateTime.now();
            return new WahlbeteiligungsMeldungDTO().wahlID(wahlID).wahlbezirkID(wahlbezirkID).anzahlWaehler(anzahlWahler)
                    .meldeZeitpunkt(meldeZeitpunkt);
        }
    }

}
