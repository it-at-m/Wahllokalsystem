package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMdcFilterTest {

    private static final String MDC_USER_KEY = "user";

    @InjectMocks
    private UserMdcFilter userMdcFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Nested
    class DoFilterInternal {

        @Test
        void should_proceedWithoutMdc_when_authIsNull() throws ServletException, IOException {
            when(securityContext.getAuthentication()).thenReturn(null);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_extractClaimAndSetMdc_when_givenJwtAuthenticationTokenWithPreferredUsername() throws ServletException, IOException {
            JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
            Jwt mockJwt = mock(Jwt.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getToken()).thenReturn(mockJwt);
            when(mockJwt.getClaimAsString("preferred_username")).thenReturn("jwt.user");

            doAnswer(invocation -> {
                assertEquals("jwt.user", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_setNameInMdc_when_jwtAuthenticationTokenClaimsAreMissingButNameIsPresent() throws ServletException, IOException {

            JwtAuthenticationToken mockAuth = mock(JwtAuthenticationToken.class);
            Jwt mockJwt = mock(Jwt.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getToken()).thenReturn(mockJwt);
            when(mockAuth.getName()).thenReturn("jwt.fallback.name");

            doAnswer(invocation -> {
                assertEquals("jwt.fallback.name", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_setUsernameInMdc_when_principalIsUserDetails() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);
            UserDetails mockUserDetails = mock(UserDetails.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn(mockUserDetails);
            when(mockUserDetails.getUsername()).thenReturn("details.user");

            doAnswer(invocation -> {
                assertEquals("details.user", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_extractAttributeAndSetMdc_when_principalIsOAuth2User() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);
            OAuth2User mockOAuth2User = mock(OAuth2User.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn(mockOAuth2User);
            when(mockOAuth2User.getAttribute("preferred_username")).thenReturn("oauth.preferred");

            doAnswer(invocation -> {
                assertEquals("oauth.preferred", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_extractPreferredUsernameAndSetMdc_when_principalIsOidcUser() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);
            OidcUser mockOidcUser = mock(OidcUser.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn(mockOidcUser);
            when(mockOidcUser.getPreferredUsername()).thenReturn("oidc.user");

            doAnswer(invocation -> {
                assertEquals("oidc.user", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_extractClaimAndSetMdc_when_principalIsPureJwt() throws ServletException, IOException {
            Authentication mockAuth = mock(Authentication.class);
            Jwt mockJwt = mock(Jwt.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn(mockJwt);

            when(mockJwt.getClaimAsString("preferred_username")).thenReturn(null);
            when(mockJwt.getClaimAsString("username")).thenReturn(null);
            when(mockJwt.getClaimAsString("upn")).thenReturn(null);
            when(mockJwt.getClaimAsString("email")).thenReturn("purejwt.email@test.com");

            doAnswer(invocation -> {
                assertEquals("purejwt.email@test.com", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_setNameInMdc_when_principalIsPurePrincipalInterface() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);
            Principal mockPrincipal = mock(Principal.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn(mockPrincipal);
            when(mockPrincipal.getName()).thenReturn("pure.principal");

            doAnswer(invocation -> {
                assertEquals("pure.principal", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_setAuthNameInMdc_when_principalTypeIsUnknown() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getPrincipal()).thenReturn("UnknownPrincipalType");
            when(mockAuth.getName()).thenReturn("auth.fallback.name");

            doAnswer(invocation -> {
                assertEquals("auth.fallback.name", MDC.get(MDC_USER_KEY));
                return null;
            }).when(filterChain).doFilter(request, response);

            userMdcFilter.doFilterInternal(request, response, filterChain);

            assertNull(MDC.get(MDC_USER_KEY));
        }

        @Test
        void should_stillClearMdc_when_filterChainThrowsException() throws ServletException, IOException {

            Authentication mockAuth = mock(Authentication.class);
            when(securityContext.getAuthentication()).thenReturn(mockAuth);
            when(mockAuth.getName()).thenReturn("error-user");

            doThrow(new RuntimeException("Database down!"))
                    .when(filterChain).doFilter(request, response);


            assertThrows(RuntimeException.class, () ->
                    userMdcFilter.doFilterInternal(request, response, filterChain)
            );

            assertNull(MDC.get(MDC_USER_KEY));
        }
    }
}
