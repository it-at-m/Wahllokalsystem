package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserMdcFilter extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(UserMdcFilter.class);
  private static final String MDC_USER_KEY = "user";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String username = extractUsername();
      LOG.debug("UserMdcFilter: resolved username={}", username);
      if (StringUtils.hasText(username)) {
        MDC.put(MDC_USER_KEY, username);
      }
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_USER_KEY);
    }
  }

  private String extractUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      LOG.debug("Auth ist null");
      return null;
    }

    if (auth instanceof AnonymousAuthenticationToken) {
      LOG.debug("UserMdcFilter: Anonyme Authentifizierung erkannt - wird übersprungen");
      return null;
    }

    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      if (jwt != null) {
        String[] claims = {"preferred_username", "username", "upn", "email", "sub"};
        for (String c : claims) {
          String v = jwt.getClaimAsString(c);
          if (StringUtils.hasText(v)) {
            return v;
          }
        }
      }
      if (StringUtils.hasText(jwtAuth.getName())) {
        return jwtAuth.getName();
      }
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof UserDetails) {
      return ((UserDetails) principal).getUsername();
    }

    if (principal instanceof OidcUser oidc) {
      if (StringUtils.hasText(oidc.getPreferredUsername()))
        return oidc.getPreferredUsername();
      if (StringUtils.hasText(oidc.getEmail())) return oidc.getEmail();
      if (StringUtils.hasText(oidc.getName())) return oidc.getName();
    }

    if (principal instanceof OAuth2User oauth) {
      Object pref = oauth.getAttribute("preferred_username");
      if (pref != null && !pref.toString().isBlank()) return pref.toString();
      Object username = oauth.getAttribute("username");
      if (username != null) return username.toString();
      Object email = oauth.getAttribute("email");
      if (email != null) return email.toString();
      if (StringUtils.hasText(oauth.getName())) return oauth.getName();
    }

    if (principal instanceof Jwt jwt) {
      String[] claims = {"preferred_username", "username", "upn", "email", "sub"};
      for (String c : claims) {
        String v = jwt.getClaimAsString(c);
        if (StringUtils.hasText(v)) return v;
      }
    }

    if (principal instanceof Principal) {
      String n = ((Principal) principal).getName();
      if (StringUtils.hasText(n)) return n;
    }

    String name = auth.getName();
    return (StringUtils.hasText(name)) ? name : null;
  }
}
