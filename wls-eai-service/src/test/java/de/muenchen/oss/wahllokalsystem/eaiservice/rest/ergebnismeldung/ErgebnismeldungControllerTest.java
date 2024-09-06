package de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung.ErgebnismeldungService;
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
    class SaveErgebnismeldung {

        @Test
        void serviceIsCalled() {
            val ergebnismeldungDTO = ErgebnismeldungDTO.builder().build();

            unitUnderTest.saveErgebnismeldung(ergebnismeldungDTO);

            Mockito.verify(ergebnismeldungService).saveErgebnismeldung(ergebnismeldungDTO);
        }
    }
}
