package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UsernameResolver {
  private static final List<String> USERNAME_CLAIMS = List.of("username", "sub");

  public String resolve() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      log.warn(
          "Authentifizierung fehlgeschlagen: SecurityContext enthält kein Authentication-Objekt.");
      return null;
    }

    if (isAnonymous(authentication)) {
      return null;
    }

    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      if (jwtAuthentication.getToken() == null) {
        log.warn(
            "Nicht unterstützter Authentifizierungstyp: JwtAuthenticationToken enthält keinen Token.");
        return null;
      }

      return firstNonBlank(
          extractFromJwt(jwtAuthentication.getToken()), jwtAuthentication.getName());
    }

    log.warn("Nicht unterstützter Authentifizierungstyp: {}", authentication.getClass().getName());
    return authentication.getName();
  }

  private boolean isAnonymous(Authentication authentication) {
    return !authentication.isAuthenticated()
        || (authentication instanceof AnonymousAuthenticationToken);
  }

  private String extractFromJwt(Jwt jwt) {
    for (String claim : USERNAME_CLAIMS) {
      String value = jwt.getClaimAsString(claim);

      if (hasText(value)) {
        return value;
      }
    }

    return null;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (hasText(value)) {
        return value;
      }
    }

    return null;
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
