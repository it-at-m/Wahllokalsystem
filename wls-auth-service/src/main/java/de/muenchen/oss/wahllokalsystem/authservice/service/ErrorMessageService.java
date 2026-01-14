package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.security.ErrorMessages;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageService {

  public String getErrorMessage(final Exception exception) {
    if (exception instanceof BadCredentialsException) {
      return exception.getMessage();
    } else if (exception instanceof LockedException) {
      return exception.getMessage();
    } else if (exception instanceof SessionAuthenticationException) {
      return ErrorMessages.BENUTZER_BEREITS_ANGEMELDET + exception.getMessage();
    } else if (exception instanceof InternalAuthenticationServiceException) {
      return ErrorMessages.LOGIN_BLOCKED_BY_LDAP_SERVER;
    } else if (exception instanceof AccessDeniedException) {
      return exception.getMessage();
    } else {
      return ErrorMessages.INVALID_USERNAME_OR_PASSWORD;
    }
  }
}
