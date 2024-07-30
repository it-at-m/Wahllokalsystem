package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlenControllerTest {

    @Mock
    WahlenService wahlenService;

    @Nested
    class ResetWahlen {

        @Test
        void serviceIsCalledWithoutExceptions() {
            Assertions.assertThatCode(() -> wahlenService.resetWahlen()).doesNotThrowAnyException();
            Mockito.verify(wahlenService).resetWahlen();
        }
    }
}
