package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import java.security.Principal;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UsernameResolver {
  private static final List<String> USERNAME_CLAIMS =
      List.of("preferred_username", "username", "upn", "email", "sub");

  public String resolve() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!isUsable(authentication)) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof OidcUser oidcUser) {
      return firstNonBlank(
          oidcUser.getPreferredUsername(), oidcUser.getEmail(), oidcUser.getName());
    }

    if (principal instanceof OAuth2User oauth2User) {
      return firstNonBlank(
          attributeAsString(oauth2User, "preferred_username"),
          attributeAsString(oauth2User, "username"),
          attributeAsString(oauth2User, "email"),
          oauth2User.getName());
    }

    if (principal instanceof Jwt jwt) {
      return extractFromJwt(jwt);
    }

    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      return firstNonBlank(
          extractFromJwt(jwtAuthentication.getToken()), jwtAuthentication.getName());
    }

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }

    if (principal instanceof Principal principalObject) {
      return principalObject.getName();
    }

    return authentication.getName();
  }

  private boolean isUsable(Authentication authentication) {
    return authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken);
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

  private String attributeAsString(OAuth2User user, String attribute) {

    Object value = user.getAttribute(attribute);

    return value != null ? value.toString() : null;
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
