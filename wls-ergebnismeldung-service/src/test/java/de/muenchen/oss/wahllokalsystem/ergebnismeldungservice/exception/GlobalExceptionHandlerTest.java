package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock ServiceIDFormatter serviceIDFormatter;

  @InjectMocks GlobalExceptionHandler unitUnderTest;

  private ListAppender<ILoggingEvent> listAppender;
  private Logger logger;

  @BeforeEach
  void setup() {
    logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void teardown() {
    logger.detachAppender(listAppender);
  }

  @Nested
  class HandleWlsExceptions {

    @Test
    void should_createResponseEntityWithWlsException_when_dataConflictExceptionIsGiven() {
      val serviceID = Instancio.create(String.class);
      val dataConflictException =
          new DataConflictException(Instancio.create(String.class), Instancio.create(String.class));

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

      // Check Logging
      Assertions.assertThat(listAppender.list).hasSize(1);
      val logEvent = listAppender.list.getFirst();
      Assertions.assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
      Assertions.assertThat(logEvent.getFormattedMessage())
          .isEqualTo(
              String.format(
                  "Datenkonflikt aufgetreten (Code: %s, Service %s): %s",
                  dataConflictException.getCode(), serviceID, dataConflictException.getMessage()));
      Assertions.assertThat(logEvent.getThrowableProxy()).isNull();
    }
  }

  @Nested
  class HandleThrowables {

    @Test
    void should_createResponseEntityWithInternalServerError_when_genericThrowableIsGiven() {
      val serviceID = Instancio.create(String.class);
      val throwable = new RuntimeException("Unerwarteter Fehler");

      Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

      val result = unitUnderTest.handleThrowables(throwable);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      Assertions.assertThat(result.getBody()).isNotNull();
      Assertions.assertThat(result.getBody().service()).isEqualTo(serviceID);

      // Check Logging
      Assertions.assertThat(listAppender.list).hasSize(1);
      val logEvent = listAppender.list.getFirst();
      Assertions.assertThat(logEvent.getLevel()).isEqualTo(Level.ERROR);
      Assertions.assertThat(logEvent.getFormattedMessage()).isEqualTo("handling throwable");
      Assertions.assertThat(logEvent.getThrowableProxy()).isNotNull();
      Assertions.assertThat(logEvent.getThrowableProxy().getMessage())
          .isEqualTo("Unerwarteter Fehler");
    }
  }
}
