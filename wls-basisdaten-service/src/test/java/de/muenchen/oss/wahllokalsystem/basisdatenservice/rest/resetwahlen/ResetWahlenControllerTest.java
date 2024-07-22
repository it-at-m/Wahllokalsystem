package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.resetwahlen;


import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.ResetWahlenController;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen.ResetWahlenService;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ResetWahlenControllerTest {

    @Mock
    ResetWahlenService resetWahlenService;

    @InjectMocks
    ResetWahlenController resetWahlenController;

    @Nested
    class ResetWahlen {

        @Test
        void serviceIsCalledWithoutExceptions() {
            Assertions.assertThatCode(() -> resetWahlenService.resetWahlen()).doesNotThrowAnyException();

            val result = resetWahlenController.resetWahlen();
            Assertions.assertThat(result).isInstanceOf(ResponseEntity.class);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }
}