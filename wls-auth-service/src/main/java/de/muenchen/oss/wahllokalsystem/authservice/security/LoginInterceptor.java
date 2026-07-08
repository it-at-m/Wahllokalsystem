package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahltagClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor {

  @Value("${service.config.loginCheckMessage}")
  private String loginCheckMessage;

  private final LoginTimeClient loginTimeClient;

  private final WahltagClient wahltagClient;

  private final UserService userService;

  public void validateLoginOrThrow(final LdapUserDetails ldapUserDetails) {
    if (isLoginTimeToCheck(ldapUserDetails)) {
      if (!isUserOfActiveElectionDay(ldapUserDetails)) {
        throw new DisabledException(loginCheckMessage);
      }

      validateNowIsInLoginIntervalOrThrow();
    }
  }

  private void validateNowIsInLoginIntervalOrThrow() {
    try {
      val legalLoginInterval = loginTimeClient.getLegalLoginInterval();

      val now = LocalDateTime.now();
      val nowIsAfterOrEqualEarliestLoginWhenEarliestExists =
          legalLoginInterval.earliestLogin() == null
              || !now.isBefore(legalLoginInterval.earliestLogin());
      val nowIsBeforeOrEqualLatestLoginWhenLatestExists =
          legalLoginInterval.latestLogin() == null
              || !now.isAfter(legalLoginInterval.latestLogin());
      if (!nowIsAfterOrEqualEarliestLoginWhenEarliestExists
          || !nowIsBeforeOrEqualLatestLoginWhenLatestExists) {
        throw new DisabledException(
            "Login ausßerhalb der gültigen Login-Zeiten zwischen "
                + legalLoginInterval.earliestLogin()
                + " und "
                + legalLoginInterval.latestLogin()
                + ".");
      }
    } catch (final WlsException wlsException) {
      log.warn(
          "Login wird erlaubt, jedoch war das Abrufen der Frühesten/Spätesten Loginuhrzeit nicht möglich. Fehlermeldung: {}",
          wlsException.getMessage(),
          wlsException);
    }
  }

  private boolean isUserOfActiveElectionDay(LdapUserDetails principal) {
    log.debug("Benutzer ist: {}", principal.getUsername());
    val userWithAuthorities = userService.getUser(principal.getUsername());
    try {
      if (userWithAuthorities.isPresent()
          && hasWahllokalAuthority(userWithAuthorities.get().authorities())) {
        return wahltagClient.isWahltagActive(userWithAuthorities.get().wahltagID());
      }
    } catch (Exception e) {
      return true;
    }
    return true;
  }

  private boolean hasWahllokalAuthority(Collection<String> authorities) {
    return authorities.contains(userService.getSchriftfuehrungAuthorityName());
  }

  private boolean isLoginTimeToCheck(LdapUserDetails principal) {
    val schriftfuehrungAuthorityName = userService.getSchriftfuehrungAuthorityName().toLowerCase();
    val adminAuthorityName = userService.getAdminAuthorityName().toLowerCase();

    for (GrantedAuthority eAuthority : principal.getAuthorities()) {
      if (eAuthority.getAuthority().toLowerCase().contains(schriftfuehrungAuthorityName)
          || eAuthority.getAuthority().toLowerCase().contains(adminAuthorityName)) {
        return true;
      }
    }
    return false;
  }
}
