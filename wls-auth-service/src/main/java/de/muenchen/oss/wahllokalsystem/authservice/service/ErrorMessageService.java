package de.muenchen.oss.wahllokalsystem.authservice.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Data
public class ErrorMessageService {

  private static final String MESSAGE_TEMPLATE_USER_IS_ALREADY_LOCKED =
      "Benutzer '%s' wurde für %s Minuten gesperrt. Die Sperre dauert noch: %s";
  private static final String MESSAGE_TEMPLATE_USER_IS_GETTING_LOCKED =
      "Falscher Benutzername oder Passwort. Ab dem nächsten Versuch wird der Zugang für %s Minuten gesperrt.";
  private static final String MESSAGE_TEMPLATE_USER_GOT_LOCKED =
      "Falscher Benutzername oder Passwort. Benutzer wurde für %s Minuten gesperrt.";

  public static final String BENUTZER_BEREITS_ANGEMELDET =
      "Es ist bereits ein Benutzer mit ihrem Benutzernamen angemeldet.";
  public static final String LOGIN_BLOCKED_BY_LDAP_SERVER = "Blockiert durch LDAP.";
  public static final String INVALID_USERNAME_OR_PASSWORD = "Falscher Benutzername oder Passwort.";
  public static final String INVALID_LOGIN_TIMES =
      "Anmeldung erfolgte ausserhalb der gültigen Login-Zeiten.";
  public static final String NOT_IN_ACTIVE_ELECTION =
      "Der Benutzer ist nicht für die aktive Wahl zugelassen.";

  @Value("${service.config.falscheLoginZeitstrafe}")
  private int falscheLoginZeitstrafeInMinutes;

  public String getErrorMessage(final Exception exception) {
    if (exception instanceof BadCredentialsException) {
      return exception.getMessage();
    } else if (exception instanceof LockedException) {
      return exception.getMessage();
    } else if (exception instanceof SessionAuthenticationException) {
      return BENUTZER_BEREITS_ANGEMELDET + exception.getMessage();
    } else if (exception instanceof InternalAuthenticationServiceException) {
      return LOGIN_BLOCKED_BY_LDAP_SERVER;
    } else if (exception instanceof AccessDeniedException) {
      return exception.getMessage();
    } else {
      return INVALID_USERNAME_OR_PASSWORD;
    }
  }

  public String getErrorMessageUserIsLocked(
      final String username, final String remainingTimeAsString) {
    return String.format(
        MESSAGE_TEMPLATE_USER_IS_ALREADY_LOCKED,
        username,
        falscheLoginZeitstrafeInMinutes,
        remainingTimeAsString);
  }

  public String getErrorMessageUserIsGettingLocked() {
    return String.format(MESSAGE_TEMPLATE_USER_IS_GETTING_LOCKED, falscheLoginZeitstrafeInMinutes);
  }

  public String getErrorMessageUserGotLocked() {
    return String.format(MESSAGE_TEMPLATE_USER_GOT_LOCKED, falscheLoginZeitstrafeInMinutes);
  }
}
