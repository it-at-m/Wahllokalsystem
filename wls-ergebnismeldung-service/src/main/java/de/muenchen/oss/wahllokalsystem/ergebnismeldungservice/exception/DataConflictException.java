package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.Getter;
import lombok.ToString;

@ToString
public class DataConflictException extends RuntimeException {

  @Getter private final String code;

  public DataConflictException(final ExceptionDataWrapper exceptionDataWrapper) {
    this(exceptionDataWrapper.message(), exceptionDataWrapper.code());
  }

  public DataConflictException(final String message, final String code) {
    super(message);
    this.code = code;
  }
}
