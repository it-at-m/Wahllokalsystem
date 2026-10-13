package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.assertj.core.api.Assertions;
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
  @Test
  void should_returnNullAndLogWarning_when_authIsNull() {
    mockAuthenticated(null);

    Assertions.assertThat(usernameResolver.resolve()).isNull();

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    Assertions.assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
    Assertions.assertThat(logEvent.getFormattedMessage()).isEqualTo("Authentifizierung fehlgeschlagen: SecurityContext enthält kein Authentication-Objekt.");
  }

  @Test
  void should_returnNull_when_authIsAnonymousAuthenticationToken() {
    AnonymousAuthenticationToken anonymousAuth =
        new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
    Assertions.assertThat(usernameResolver.resolve()).isNull();
  }

  @Test
  void should_extractUsernameClaim_when_givenJwtAuthenticationToken() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);
    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("username")).thenReturn("jwt.user");

    Assertions.assertThat(usernameResolver.resolve()).isEqualTo("jwt.user");
  }

  @Test
  void should_fallbackToSubClaim_when_usernameClaimIsMissing() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);

    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);
    when(mockJwt.getClaimAsString("sub")).thenReturn("25983741-efab-4122");

    Assertions.assertThat(usernameResolver.resolve()).isEqualTo("25983741-efab-4122");
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

    Assertions.assertThat(usernameResolver.resolve()).isEqualTo("fallback.jwt.name");
  }

  @Test
  void should_returnNullAndLogWarning_when_jwtAuthenticationTokenContainsNoToken() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    mockAuthenticated(mockAuth);
    when(mockAuth.getToken()).thenReturn(null);

    Assertions.assertThat(usernameResolver.resolve()).isNull();

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    Assertions.assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
    Assertions.assertThat(logEvent.getFormattedMessage()).isEqualTo("Nicht unterstützter Authentifizierungstyp: JwtAuthenticationToken enthält keinen Token.");
  }

  @Test
  void should_returnAuthNameAndLogWarning_when_authTypeIsUnknown() {
    mockAuthenticated(authentication);
    when(authentication.getName()).thenReturn("auth.fallback.name");

    Assertions.assertThat(usernameResolver.resolve()).isEqualTo("auth.fallback.name");

    verify(mockAppender).doAppend(logEventCaptor.capture());
    ILoggingEvent logEvent = logEventCaptor.getValue();

    Assertions.assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);

    String expectedPrefix =
        "Nicht unterstützter Authentifizierungstyp: " + authentication.getClass().getName();
    Assertions.assertThat(logEvent.getFormattedMessage()).isEqualTo(expectedPrefix);
  }

  private void mockAuthenticated(Authentication auth) {
    when(securityContext.getAuthentication()).thenReturn(auth);
    if (auth != null) {
      when(auth.isAuthenticated()).thenReturn(true);
    }
  }
}
