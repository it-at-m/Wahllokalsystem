package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungControllerTest {

    @Mock
    ErgebnismeldungService ergebnismeldungService;

    @InjectMocks
    ErgebnismeldungController unitUnderTest;

    @Nested
    class SendErgebnisse {

        @Test
        void should_callForceErgebnisse_when_forceIsTrueInAllLowerCase() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val sendErgebnisParameter = new SendErgebnisParameter(wahlID, wahlbezirkID, null, null, null);

            unitUnderTest.sendErgebnisse("true", sendErgebnisParameter);

            Mockito.verify(ergebnismeldungService).updateSendungszeiten(new BezirkUndWahlID(wahlID, wahlbezirkID));
        }

        @Test
        void should_callForceErgebnisse_when_forceIsTrueInAllUpperCase() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val sendErgebnisParameter = new SendErgebnisParameter(wahlID, wahlbezirkID, null, null, null);

            unitUnderTest.sendErgebnisse("TRUE", sendErgebnisParameter);

            Mockito.verify(ergebnismeldungService).updateSendungszeiten(new BezirkUndWahlID(wahlID, wahlbezirkID));
        }
    }
}
