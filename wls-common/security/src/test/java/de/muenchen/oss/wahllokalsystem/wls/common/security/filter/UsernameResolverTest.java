package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class UsernameResolverTest {

  private UsernameResolver usernameResolver;

  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @BeforeEach
  void setup() {
    usernameResolver = new UsernameResolver();
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void teardown() {
    SecurityContextHolder.clearContext();
  }

  private void mockAuthenticated(Authentication auth) {
    when(securityContext.getAuthentication()).thenReturn(auth);
    if (auth != null) {
      when(auth.isAuthenticated()).thenReturn(true);
    }
  }

  @Test
  void should_returnNull_when_authIsNull() {
    mockAuthenticated(null);
    assertNull(usernameResolver.resolve());
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
  void should_extractPreferredUsername_when_principalIsOidcUser() {
    OidcUser mockOidcUser = mock(OidcUser.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOidcUser);
    when(mockOidcUser.getPreferredUsername()).thenReturn("oidc.user");

    assertEquals("oidc.user", usernameResolver.resolve());
  }

  @Test
  void should_extractAttribute_when_principalIsOAuth2User() {
    OAuth2User mockOAuth2User = mock(OAuth2User.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOAuth2User);
    when(mockOAuth2User.getAttribute("preferred_username")).thenReturn("oauth.preferred");

    assertEquals("oauth.preferred", usernameResolver.resolve());
  }

  @Test
  void should_extractClaim_when_principalIsPureJwt() {
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockJwt);

    when(mockJwt.getClaimAsString("preferred_username")).thenReturn(null);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);
    when(mockJwt.getClaimAsString("upn")).thenReturn(null);

    when(mockJwt.getClaimAsString("email")).thenReturn("purejwt.email@test.com");

    assertEquals("purejwt.email@test.com", usernameResolver.resolve());
  }

  @Test
  void should_extractClaim_when_givenJwtAuthenticationToken() {
    JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(mockAuth);
    when(mockAuth.getToken()).thenReturn(mockJwt);
    when(mockJwt.getClaimAsString("preferred_username")).thenReturn("jwt.user");

    assertEquals("jwt.user", usernameResolver.resolve());
  }

  @Test
  void should_setUsername_when_principalIsUserDetails() {
    UserDetails mockUserDetails = mock(UserDetails.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockUserDetails);
    when(mockUserDetails.getUsername()).thenReturn("details.user");

    assertEquals("details.user", usernameResolver.resolve());
  }

  @Test
  void should_setName_when_principalIsPurePrincipalInterface() {
    Principal mockPrincipal = mock(Principal.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockPrincipal);
    when(mockPrincipal.getName()).thenReturn("pure.principal");

    assertEquals("pure.principal", usernameResolver.resolve());
  }

  @Test
  void should_setAuthName_when_principalTypeIsUnknown() {
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn("UnknownPrincipalType");
    when(authentication.getName()).thenReturn("auth.fallback.name");

    assertEquals("auth.fallback.name", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToEmail_when_oidcUserPreferredUsernameIsBlank() {
    OidcUser mockOidcUser = mock(OidcUser.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOidcUser);

    when(mockOidcUser.getPreferredUsername()).thenReturn("   ");
    when(mockOidcUser.getEmail()).thenReturn("fallback.email@test.com");

    assertEquals("fallback.email@test.com", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToName_when_oidcUserPreferredUsernameAndEmailAreNull() {
    OidcUser mockOidcUser = mock(OidcUser.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOidcUser);

    when(mockOidcUser.getPreferredUsername()).thenReturn(null);
    when(mockOidcUser.getEmail()).thenReturn(null);
    when(mockOidcUser.getName()).thenReturn("fallback.name");

    assertEquals("fallback.name", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToUsernameAttribute_when_oauth2UserPreferredUsernameIsNull() {
    OAuth2User mockOAuth2User = mock(OAuth2User.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOAuth2User);

    when(mockOAuth2User.getAttribute("preferred_username")).thenReturn(null);
    when(mockOAuth2User.getAttribute("username")).thenReturn("oauth.username.fallback");
    when(mockOAuth2User.getAttribute("email")).thenReturn("should.not.be.chosen@test.com");

    assertEquals("oauth.username.fallback", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToGetName_when_allOAuth2UserAttributesAreMissing() {
    OAuth2User mockOAuth2User = mock(OAuth2User.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockOAuth2User);

    when(mockOAuth2User.getAttribute("preferred_username")).thenReturn(null);
    when(mockOAuth2User.getAttribute("username")).thenReturn(null);
    when(mockOAuth2User.getAttribute("email")).thenReturn(null);

    when(mockOAuth2User.getName()).thenReturn("oauth.technical.id.123");

    assertEquals("oauth.technical.id.123", usernameResolver.resolve());
  }

  @Test
  void should_respectClaimPriorityOrder_when_principalIsPureJwt() {
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockJwt);

    when(mockJwt.getClaimAsString("preferred_username")).thenReturn(null);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);

    when(mockJwt.getClaimAsString("upn")).thenReturn("user.upn@domain.local");

    assertEquals("user.upn@domain.local", usernameResolver.resolve());
  }

  @Test
  void should_fallbackToSubClaim_when_allOtherJwtClaimsAreMissing() {
    Jwt mockJwt = mock(Jwt.class);
    mockAuthenticated(authentication);
    when(authentication.getPrincipal()).thenReturn(mockJwt);

    when(mockJwt.getClaimAsString("preferred_username")).thenReturn(null);
    when(mockJwt.getClaimAsString("username")).thenReturn(null);
    when(mockJwt.getClaimAsString("upn")).thenReturn(null);
    when(mockJwt.getClaimAsString("email")).thenReturn(null);

    when(mockJwt.getClaimAsString("sub")).thenReturn("25983741-efab-4122");

    assertEquals("25983741-efab-4122", usernameResolver.resolve());
  }
}
