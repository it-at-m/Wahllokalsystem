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
public class LoginInterceptorService {

    private static final String MONITORING_AUTHORITY = "MONITORING".toLowerCase();
    private static final String WAHLVORSTAND_AUTHORITY = "WAHLVORSTAND".toLowerCase();
    private static final String ROLE_LOGIN_WLS_WAHLLOKAL = "WLS_WAHLVORSTAND";

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
            val nowIsAfterOrEqualEarliestLoginWhenEarliestExists = legalLoginInterval.earliestLogin() == null || !now.isBefore(
                    legalLoginInterval.earliestLogin());
            val nowIsBeforeOrEqualLatestLoginWhenLatestExists = legalLoginInterval.latestLogin() == null || !now.isAfter(legalLoginInterval.latestLogin());
            if (!nowIsAfterOrEqualEarliestLoginWhenEarliestExists || !nowIsBeforeOrEqualLatestLoginWhenLatestExists) {
                throw new DisabledException(
                        "Login ausßerhalb der gültigen Login-Zeiten zwischen " + legalLoginInterval.earliestLogin() + " und " + legalLoginInterval.latestLogin()
                                + ".");
            }
        } catch (final WlsException wlsException) {
            log.warn("Login wird erlaubt, jedoch war das Abrufen der Frühesten/Spätesten Loginuhrzeit nicht möglich. Fehlermeldung: {}",
                    wlsException.getMessage(), wlsException);
        }
    }

    private boolean isUserOfActiveElectionDay(LdapUserDetails principal) {
        log.debug("Benutzer ist: {}", principal.getUsername());
        val userWithAuthorities = userService.getUser(principal.getUsername());
        try {
            if (userWithAuthorities.isPresent() && hasWahllokalAuthority(userWithAuthorities.get().authorities())) {
                return wahltagClient.isWahltagActive(userWithAuthorities.get().wahltagID());
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    private boolean hasWahllokalAuthority(Collection<String> authorities) {
        return authorities.stream().anyMatch(ROLE_LOGIN_WLS_WAHLLOKAL::equals);
    }

    private boolean isLoginTimeToCheck(LdapUserDetails principal) {
        for (GrantedAuthority eAuthority : principal.getAuthorities()) {
            if (eAuthority.getAuthority().toLowerCase().contains(WAHLVORSTAND_AUTHORITY)
                    || eAuthority.getAuthority().toLowerCase().contains(MONITORING_AUTHORITY)) {
                return true;
            }
        }
        return false;
    }
}
