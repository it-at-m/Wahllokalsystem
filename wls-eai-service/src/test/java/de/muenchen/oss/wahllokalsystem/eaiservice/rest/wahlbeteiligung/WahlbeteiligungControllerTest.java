package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung.WahlbeteiligungService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlbeteiligungControllerTest {

    @Mock
    WahlbeteiligungService wahlbeteiligungService;

    @InjectMocks
    WahlbeteiligungController unitUnderTest;

    @Nested
    class SaveWahlbeteiligunsMeldung {

        @Test
        void serviceIsCalled() {

            val wahlbeteiligungsMeldungDTO = WahlbeteiligungsMeldungDTO.builder().build();

            unitUnderTest.saveWahlbeteiligung(wahlbeteiligungsMeldungDTO);

            Mockito.verify(wahlbeteiligungService).saveWahlbeteiligung(wahlbeteiligungsMeldungDTO);
        }

    }
}
