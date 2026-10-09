package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class UsernameResolverTest {

  private UsernameResolver usernameResolver;

  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @Mock private Appender<ILoggingEvent> mockAppender;
  @Captor private ArgumentCaptor<ILoggingEvent> logEventCaptor;
  private Logger logger;

  @BeforeEach
  void setup() {
    usernameResolver = new UsernameResolver();
    SecurityContextHolder.setContext(securityContext);

    logger = (Logger) LoggerFactory.getLogger(UsernameResolver.class);
    logger.addAppender(mockAppender);
  }

  @AfterEach
  void teardown() {
    SecurityContextHolder.clearContext();
    logger.detachAppender(mockAppender);
  }

  private void mockAuthenticated(Authentication auth) {
    when(securityContext.getAuthentication()).thenReturn(auth);
    if (auth != null) {
      when(auth.isAuthenticated()).thenReturn(true);
    }
  }

  @Test
  void should_returnNullAndLogWarning_when_authIsNull() {
    mockAuthenticated(null);

    assertNull(usernameResolver.resolve());

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals(
        "Authentifizierung fehlgeschlagen: SecurityContext enthält kein Authentication-Objekt.",
        logEvent.getFormattedMessage());
  }

  @Test
  void should_returnNull_when_authIsAnonymousAuthenticationToken() {
    AnonymousAuthenticationToken anonymousAuth =
        new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
    assertNull(usernameResolver.resolve());
  }

  @Test
  void should_extractUsernameClaim_when_givenJwtAuthenticationToken() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);
    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("username")).thenReturn("jwt.user");

    assertEquals("jwt.user", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToSubClaim_when_usernameClaimIsMissing() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);

    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);
    when(mockJwt.getClaimAsString("sub")).thenReturn("25983741-efab-4122");

    assertEquals("25983741-efab-4122", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToAuthName_when_allJwtClaimsAreMissing() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);

    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);
    when(mockJwt.getClaimAsString("sub")).thenReturn(null);
    when(mockAuth.getName()).thenReturn("fallback.jwt.name");

    assertEquals("fallback.jwt.name", usernameResolver.resolve());
  }

  @Test
  void should_returnNullAndLogWarning_when_jwtAuthenticationTokenContainsNoToken() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    mockAuthenticated(mockAuth);
    when(mockAuth.getToken()).thenReturn(null);

    assertNull(usernameResolver.resolve());

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals(
        "Nicht unterstützter Authentifizierungstyp: JwtAuthenticationToken enthält keinen Token.",
        logEvent.getFormattedMessage());
  }

  @Test
  void should_returnAuthNameAndLogWarning_when_authTypeIsUnknown() {
    mockAuthenticated(authentication);
    when(authentication.getName()).thenReturn("auth.fallback.name");

    assertEquals("auth.fallback.name", usernameResolver.resolve());

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    assertEquals(Level.WARN, logEvent.getLevel());

    String expectedPrefix =
        "Nicht unterstützter Authentifizierungstyp: " + authentication.getClass().getName();
    assertEquals(expectedPrefix, logEvent.getFormattedMessage());
  }
}
