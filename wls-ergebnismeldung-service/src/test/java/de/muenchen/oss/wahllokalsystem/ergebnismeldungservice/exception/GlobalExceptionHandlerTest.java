package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock ServiceIDFormatter serviceIDFormatter;

  @InjectMocks GlobalExceptionHandler unitUnderTest;

  @Nested
  class HandleWlsExceptions {

    @Test
    void should_createResponseEntityWithWlsException_when_dataConflictExceptionIsGiven() {
      val serviceID = Instancio.create(String.class);
      val dataConflictException = Instancio.create(DataConflictException.class);

      Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

      val result = unitUnderTest.handleWlsExceptions(dataConflictException);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              dataConflictException.getCode(),
              serviceID,
              dataConflictException.getMessage());
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      Assertions.assertThat(result.getBody()).isEqualTo(expectedWlsExceptionDTO);
    }
  }
}
