package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.LoginAttemptModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger SYSLOGGER = LoggerFactory.getLogger("AUTH_SERVICE_SIEM_LOGGER");

    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;

    private static final String SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
    private static final String ROLE_LOGIN_ADMINTOOL_POLYMER = "MONITORING_HELPDESK";
    private static final String ROLE_LOGIN_WLS_WAHLLOKAL = "WLS_WAHLVORSTAND";

    @Value("${service.config.oauth2.clients.wahllokalgui.id}")
    String wahllokalguiClientId;

    @Value("${service.config.oauth2.clients.admingui.id}")
    String adminguiClientId;

    @Value("${service.config.maxLoginAttempts}")
    int maxLoginAttempts;

    @Value("${service.config.falscheLoginZeitstrafe}")
    int falscheLoginZeitstrafeInMinutes;

    @Value("${service.config.loginCheckMessage}")
    String loginCheckMessage;

    private final UserService userService;

    private final LoginInterceptorService loginInterceptorService;

    public CustomUsernamePasswordAuthenticationFilter(final UserService userService, final LoginInterceptorService loginInterceptorService,
            final AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.userService = userService;
        this.loginInterceptorService = loginInterceptorService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        val username = obtainUsername(request);

        log.debug("Checking if user {} is blocked", username);
        if (userService.isLocked(username)) {
            log.debug("User '{}' is locked!", username);
            log.debug("Checking if user {} has still time penalty", username);

            val loginAttempts = userService.getUserAttempts(username);
            if (loginAttempts.isPresent() && loginAttempts.get().lastModified() != null && !isPenaltyOver(loginAttempts.get().lastModified())) {
                String sperreDauer = getSperredauer(loginAttempts.get().lastModified());
                throw new LockedException(username + ErrorMessages.BENUTZER_WURDE_GESPERRT_DAUERT + sperreDauer);
            }

            logUserCustom("5", "benutzername=" + username + "|message=Der Benutzer ist gesperrt|", username);
        }

        log.debug("Checking if user {} is allowed to log in", username);
        if (!isUserAllowedToLogin(request)) {
            userService.updateFailAttempts(username);

            logUserCustom("6", "benutzername=" + username + "|message=Dem Benutzer ist eine Anmeldung nicht erlaubt|", username);

            throw new BadCredentialsException(ErrorMessages.INVALID_USERNAME_OR_PASSWORD);
        }

        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        val principal = (LdapUserDetails) authResult.getPrincipal();
        val username = principal.getUsername();
        userService.resetFailAttempts(username);

        try {
            loginInterceptorService.validateLoginOrThrow(principal);
            logUserCustom("0", "benutzername=" + username + "|message=Der Benutzer hat sich erfolgreich am System angemeldet|", username);
            super.successfulAuthentication(request, response, chain, authResult);

        } catch (AuthenticationException e) {
            log.error("Authentication error: " + e.getMessage(), e);
            unsuccessfulAuthentication(request, response, e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        val username = request.getParameter("username");
        if (userService.doesUserExist(username) && failed instanceof BadCredentialsException) {
            userService.updateFailAttempts(username);
            val loginAttempts = userService.getUserAttempts(username);
            val loginErrorMessage = createErrorMessage(loginAttempts);
            super.unsuccessfulAuthentication(request, response, new BadCredentialsException(loginErrorMessage));
        } else if (failed instanceof DisabledException) {
            final String message;
            if (failed.getMessage().equals(loginCheckMessage)) {
                message = ErrorMessages.NOT_IN_ACTIVE_ELECTION;
            } else {
                message = ErrorMessages.INVALID_LOGIN_TIMES;
            }
            super.unsuccessfulAuthentication(request, response, new BadCredentialsException(message));
        } else {
            log.error("Authentication failed: " + failed.getMessage());
            super.unsuccessfulAuthentication(request, response, failed);
        }

        if (userService.doesUserExist(username)) {
            logUserCustom("3", "benutzername=" + username + "|message=Falsches Passwort|", username);
        } else {
            logUserCustom("2", "benutzername=" + username + "|message=Unbekannter Benutzername|", username);
        }
    }

    private String createErrorMessage(final Optional<LoginAttemptModel> loginAttempt) {
        if (loginAttempt.isEmpty()) {
            return ErrorMessages.INVALID_USERNAME_OR_PASSWORD;
        }

        val countLoginAttempts = loginAttempt.get().attempts();

        if (countLoginAttempts == (maxLoginAttempts - 1)) {
            return ErrorMessages.BENUTZER_WIRD_GESPERRT;
        } else if (countLoginAttempts >= maxLoginAttempts) {
            return ErrorMessages.BENUTZER_WURDE_GESPERRT;
        } else {
            return ErrorMessages.INVALID_USERNAME_OR_PASSWORD;
        }
    }

    private String getSperredauer(final LocalDateTime lastLoginAttempt) {
        val endOfLoginBan = lastLoginAttempt.plusMinutes(falscheLoginZeitstrafeInMinutes);
        var now = LocalDateTime.now();

        long minutes = now.until(endOfLoginBan, ChronoUnit.MINUTES) % MINUTES_PER_HOUR;
        long seconds = now.until(endOfLoginBan, ChronoUnit.SECONDS) % SECONDS_PER_MINUTE;

        return minutes + " Minuten " + seconds + " Sekunden.";
    }

    private boolean isUserAllowedToLogin(HttpServletRequest request) {
        String username = obtainUsername(request);
        UserDetails userDetails = userService.getUserDetails(username);
        SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute(SPRING_SECURITY_SAVED_REQUEST);

        if (savedRequest == null || StringUtils.isEmpty(savedRequest.getRedirectUrl())) {
            return false;
        }

        if (savedRequest.getRedirectUrl().contains(wahllokalguiClientId)) {
            return hasRequiredAuthority(userDetails, ROLE_LOGIN_WLS_WAHLLOKAL);
        } else if (savedRequest.getRedirectUrl().contains(adminguiClientId)) {
            return hasRequiredAuthority(userDetails, ROLE_LOGIN_ADMINTOOL_POLYMER);
        }

        return false;
    }

    private boolean isPenaltyOver(LocalDateTime lastLoginAttempt) {
        return lastLoginAttempt.plusMinutes(falscheLoginZeitstrafeInMinutes).isBefore(LocalDateTime.now());
    }

    private void logUserCustom(String result, String message, String username) {
        try {
            MDC.put("eid", "LOGIN");
            MDC.put("principal", username);
            MDC.put("result", result);
            SYSLOGGER.info(message);
        } finally {
            MDC.remove("eid");
            MDC.remove("principal");
            MDC.remove("result");
        }
    }

    private boolean hasRequiredAuthority(final UserDetails userDetails, final String requiredAuthority) {
        return userDetails != null && userDetails.getAuthorities() != null && userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(requiredAuthority::equals);
    }
}
