package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenService;
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
    class LoadWahltermindaten {

        @Test
        void should_callServiceWithWahltagID_when_calledWithWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.loadWahltermindaten(wahltagID);

            Mockito.verify(wahltermindatenService).loadWahltermindaten(wahltagID);
        }
    }
}
