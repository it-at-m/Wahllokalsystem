package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    DTOMapper dtoMapper;

    @Mock
    ServiceIDFormatter serviceIDFormatter;

    @InjectMocks
    GlobalExceptionHandler unitUnderTest;

    @Test
    void handleNotFoundException() {
        val result = unitUnderTest.handleNotFoundException(new NotFoundException(UUID.randomUUID(), Object.class));

        Assertions.assertThat(result).isEqualTo(ResponseEntity.notFound().build());
    }

}
