package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.WahltermindatenService;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahltermindatenControllerTest {

    @Mock
    WahltermindatenService wahltermindatenService;

    @InjectMocks
    WahltermindatenController unitUnderTest;

    @Nested
    class PutWahltermindaten {

        @Test
        void should_callServiceWithWahltagID_when_givenWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.putWahltermindaten(wahltagID);

            Mockito.verify(wahltermindatenService).putWahltermindaten(wahltagID);
        }
    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        void should_callServiceWahltagID_when_givenWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteWahltermindaten(wahltagID);

            Mockito.verify(wahltermindatenService).deleteWahltermindaten(wahltagID);
        }

    }

}
