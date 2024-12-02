package de.muenchen.oss.wahllokalsystem.authservice.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;

import de.muenchen.oss.wahllokalsystem.authservice.service.LoginAttemptModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import jakarta.servlet.FilterChain;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

@ExtendWith(MockitoExtension.class)
class CustomUsernamePasswordAuthenticationFilterTest {

    private static final String WAHLLOKAL_GUI_CLIENT_ID = "wahllokalguiClientId";
    private static final String ADMIN_GUI_CLIENT_ID = "adminguiClientId";
    private static final int MAX_LOGINT_ATTEMPTS = 3;
    private static final int FALSCHE_LOGIN_ZEITSTRAFE = 2;
    private static final String LOGIN_CHECK_MESSAGE = "loginCheckMessage";
    private static final String SESSION_ATTRIBUTE_SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Mock
    UserService userService;

    @Mock
    LoginInterceptorService loginInterceptorService;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    @Spy
    CustomUsernamePasswordAuthenticationFilter unitUnderTest;

    @BeforeEach
    void setup() {
        unitUnderTest.wahllokalguiClientId = WAHLLOKAL_GUI_CLIENT_ID;
        unitUnderTest.adminguiClientId = ADMIN_GUI_CLIENT_ID;
        unitUnderTest.maxLoginAttempts = MAX_LOGINT_ATTEMPTS;
        unitUnderTest.falscheLoginZeitstrafeInMinutes = FALSCHE_LOGIN_ZEITSTRAFE;
        unitUnderTest.loginCheckMessage = LOGIN_CHECK_MESSAGE;
    }

    @Nested
    class AttemptAuthentication {

        @ParameterizedTest
        @ValueSource(strings = { WAHLLOKAL_GUI_CLIENT_ID, ADMIN_GUI_CLIENT_ID })
        void should_returnAuthenticationOfAppClient_when_userIsNotLockedAndIsAllowedToLogin(final String clientId) {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, clientId);

            val mockedUserDetails = new User(username, password,
                    List.of(new SimpleGrantedAuthority("WLS_WAHLVORSTAND"), new SimpleGrantedAuthority("MONITORING_HELPDESK")));
            val mockedAuthentication = Mockito.mock(Authentication.class);

            Mockito.when(userService.isLocked(username)).thenReturn(false);
            Mockito.when(userService.getUserDetails(username)).thenReturn(mockedUserDetails);
            Mockito.when(authenticationManager.authenticate(any())).thenReturn(mockedAuthentication);

            val result = unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse());

            Assertions.assertThat(result).isSameAs(mockedAuthentication);
        }

        @Test
        void should_returnAuthentication_when_userIsLockedButPenaltyIsOver() {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, WAHLLOKAL_GUI_CLIENT_ID);

            val mockedUserDetails = new User(username, password, true, true, true, false,
                    List.of(new SimpleGrantedAuthority("WLS_WAHLVORSTAND")));
            val mockedAuthentication = Mockito.mock(Authentication.class);
            val mockedLoginAttempts = new LoginAttemptModel(UUID.randomUUID(), username, 1, LocalDateTime.now().minusYears(1));

            Mockito.when(userService.isLocked(username)).thenReturn(true);
            Mockito.when(userService.getUserDetails(username)).thenReturn(mockedUserDetails);
            Mockito.when(userService.getUserAttempts(username)).thenReturn(Optional.of(mockedLoginAttempts));
            Mockito.when(authenticationManager.authenticate(any())).thenReturn(mockedAuthentication);

            val result = unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse());

            Assertions.assertThat(result).isSameAs(mockedAuthentication);
        }

        @Test
        void should_throwLockedExceptionWithUsername_when_userIsLockendAndPenaltyIsNotOver() {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, WAHLLOKAL_GUI_CLIENT_ID);

            val mockedLoginAttempts = new LoginAttemptModel(UUID.randomUUID(), username, 1, LocalDateTime.now());

            Mockito.when(userService.isLocked(username)).thenReturn(true);
            Mockito.when(userService.getUserAttempts(username)).thenReturn(Optional.of(mockedLoginAttempts));

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse()))
                    .isInstanceOf(LockedException.class)
                    .withMessageContaining(username);
        }

        @Test
        void should_throwBadCredentialsExceptionAndUpdateLoginAttempts_when_userIsNotAllowedToLoginCauseOfMissingSavedRequestInSession() {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, WAHLLOKAL_GUI_CLIENT_ID);
            httpServletRequest.getSession().setAttribute(SESSION_ATTRIBUTE_SPRING_SECURITY_SAVED_REQUEST, null);

            Mockito.when(userService.isLocked(username)).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse()))
                    .isInstanceOf(BadCredentialsException.class)
                    .withMessage(ErrorMessages.INVALID_USERNAME_OR_PASSWORD);
        }

        @ParameterizedTest
        @MethodSource("createIllegalRedirectUrls")
        void should_throwBadCredentialsExceptionAndUpdateLoginAttempts_when_userIsNotAllowedToLoginCauseOfIllegalRedirectUrl(final String illegalUrl) {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, illegalUrl);

            Mockito.when(userService.isLocked(username)).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse()))
                    .isInstanceOf(BadCredentialsException.class)
                    .withMessage(ErrorMessages.INVALID_USERNAME_OR_PASSWORD);
        }

        @ParameterizedTest
        @ValueSource(strings = { WAHLLOKAL_GUI_CLIENT_ID, ADMIN_GUI_CLIENT_ID, "someUndefinedValueContainedInRedirectUrl" })
        void should_throwBadCredentialsExceptionAndUpdateLoginAttempts_when_userIsNotAllowedToLoginCauseOfMissingAuthorityForRedirectUrl(
                final String stringContainedInRedirectURL) {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, stringContainedInRedirectURL);

            val mockedUserDetails = new User(username, password, true, true, true, false,
                    Collections.emptyList());
            Mockito.when(userService.getUserDetails(username)).thenReturn(mockedUserDetails);

            Mockito.when(userService.isLocked(username)).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.attemptAuthentication(httpServletRequest, new MockHttpServletResponse()))
                    .isInstanceOf(BadCredentialsException.class)
                    .withMessage(ErrorMessages.INVALID_USERNAME_OR_PASSWORD);
        }

        public static Stream<Arguments> createIllegalRedirectUrls() {
            return Stream.of(Arguments.of((String) null), Arguments.of(""));
        }

    }

    @Nested
    class SuccessfulAuthentication {

        @Test
        void should_resetFailAttempts_when_successfullyAuthenticates() throws Exception {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, "");

            val mockedFilterChain = Mockito.mock(FilterChain.class);
            val mockedAuthentication = Mockito.mock(Authentication.class);
            val mockedLdapUserDetails = Mockito.mock(LdapUserDetails.class);

            Mockito.when(mockedLdapUserDetails.getUsername()).thenReturn(username);
            Mockito.when(mockedAuthentication.getPrincipal()).thenReturn(mockedLdapUserDetails);

            unitUnderTest.successfulAuthentication(httpServletRequest, new MockHttpServletResponse(), mockedFilterChain, mockedAuthentication);

            Mockito.verify(loginInterceptorService).validateLoginOrThrow(mockedLdapUserDetails);
            Mockito.verify(userService).resetFailAttempts(username);
            Mockito.verify(unitUnderTest).successfulAuthentication(same(httpServletRequest), any(), same(mockedFilterChain), same(mockedAuthentication));
        }

        @Test
        void should_uncessfulAuthenticate_when_loginInterceptorThrewAuthenticationException() throws Exception {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, "");

            val mockedFilterChain = Mockito.mock(FilterChain.class);
            val mockedAuthentication = Mockito.mock(Authentication.class);
            val mockedLdapUserDetails = Mockito.mock(LdapUserDetails.class);
            val mockedValidationException = new BadCredentialsException("validation failed");

            Mockito.when(mockedLdapUserDetails.getUsername()).thenReturn(username);
            Mockito.when(mockedAuthentication.getPrincipal()).thenReturn(mockedLdapUserDetails);
            Mockito.doThrow(mockedValidationException).when(loginInterceptorService).validateLoginOrThrow(mockedLdapUserDetails);

            unitUnderTest.successfulAuthentication(httpServletRequest, new MockHttpServletResponse(), mockedFilterChain, mockedAuthentication);

            Mockito.verify(userService).resetFailAttempts(username);
            Mockito.verify(unitUnderTest).unsuccessfulAuthentication(same(httpServletRequest), any(), same(mockedValidationException));
        }

        @Test
        void should_throwException_when_loginInterceptorThrewValidationException() throws Exception {
            val username = "username";
            val password = "password";
            val httpServletRequest = createAuthenticationRequest(username, password, "");

            val mockedFilterChain = Mockito.mock(FilterChain.class);
            val mockedAuthentication = Mockito.mock(Authentication.class);
            val mockedLdapUserDetails = Mockito.mock(LdapUserDetails.class);
            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.when(mockedLdapUserDetails.getUsername()).thenReturn(username);
            Mockito.when(mockedAuthentication.getPrincipal()).thenReturn(mockedLdapUserDetails);
            Mockito.doThrow(mockedValidationException).when(loginInterceptorService).validateLoginOrThrow(mockedLdapUserDetails);

            Assertions.assertThatException().isThrownBy(
                    () -> unitUnderTest.successfulAuthentication(httpServletRequest, new MockHttpServletResponse(), mockedFilterChain, mockedAuthentication))
                    .isSameAs(mockedValidationException);
        }
    }

    @Nested
    class UnsuccessfulAuthentication {

        @Test
        void should_resetFailAttempts_when_userExistsAndExceptionWasBadCredentialsException() throws Exception {
            val username = "username";
            val httpServletRequest = createAuthenticationRequest(username, "", "");

            Mockito.when(userService.doesUserExist(username)).thenReturn(true);

            unitUnderTest.unsuccessfulAuthentication(httpServletRequest, new MockHttpServletResponse(), new BadCredentialsException(""));

            Mockito.verify(userService).updateFailAttempts(username);
        }

        @ParameterizedTest
        @MethodSource("createExpectedLoginErrorMessageForLoginAttempt")
        void should_callSuperUnsuccessfulAuthenticationWithBadCredentialsExceptionWithSpecificMessage_when_exceptionIsBadCredentialsExceptionWithSpecificLoginAttempts(
                final Optional<LoginAttemptModel> loginAttempt, final String expectedMessage)
                throws Exception {
            val username = "username";
            val httpServletRequest = createAuthenticationRequest(username, "", "");

            Mockito.when(userService.doesUserExist(username)).thenReturn(true);
            Mockito.when(userService.getUserAttempts(username)).thenReturn(loginAttempt);
            val mockedAuthenticationFailureHandler = Mockito.mock(AuthenticationFailureHandler.class);
            unitUnderTest.setAuthenticationFailureHandler(mockedAuthenticationFailureHandler);

            unitUnderTest.unsuccessfulAuthentication(httpServletRequest, new MockHttpServletResponse(), new BadCredentialsException(""));

            val exceptionArgumentCaptor = ArgumentCaptor.forClass(BadCredentialsException.class);
            Mockito.verify(mockedAuthenticationFailureHandler)
                    .onAuthenticationFailure(same(httpServletRequest), any(), exceptionArgumentCaptor.capture());
            Assertions.assertThat(exceptionArgumentCaptor.getValue().getMessage()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @MethodSource("createExpectedLoginErrorMessageForDisabledExceptionWithMessage")
        void should_callSuperUnsuccessfulAuthenticationWithBadCredentialsExceptionWithSpecificMessage_when_exceptionIsDisabledExceptionWithSpecificMessage(
                final DisabledException disabledException, final String expectedMessage)
                throws Exception {
            val username = "username";
            val httpServletRequest = createAuthenticationRequest(username, "", "");

            val mockedAuthenticationFailureHandler = Mockito.mock(AuthenticationFailureHandler.class);
            unitUnderTest.setAuthenticationFailureHandler(mockedAuthenticationFailureHandler);

            unitUnderTest.unsuccessfulAuthentication(httpServletRequest, new MockHttpServletResponse(), disabledException);

            val exceptionArgumentCaptor = ArgumentCaptor.forClass(BadCredentialsException.class);
            Mockito.verify(mockedAuthenticationFailureHandler)
                    .onAuthenticationFailure(same(httpServletRequest), any(), exceptionArgumentCaptor.capture());
            Assertions.assertThat(exceptionArgumentCaptor.getValue().getMessage()).isEqualTo(expectedMessage);
        }

        @Test
        void should_callUnsuccessfulAuthenticationWithException_when_notHandledBefore() throws Exception {
            val username = "username";
            val httpServletRequest = createAuthenticationRequest(username, "", "");
            val authenticationException = Mockito.mock(AuthenticationException.class);

            val mockedAuthenticationFailureHandler = Mockito.mock(AuthenticationFailureHandler.class);
            unitUnderTest.setAuthenticationFailureHandler(mockedAuthenticationFailureHandler);

            unitUnderTest.unsuccessfulAuthentication(httpServletRequest, new MockHttpServletResponse(), authenticationException);

            val exceptionArgumentCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
            Mockito.verify(mockedAuthenticationFailureHandler)
                    .onAuthenticationFailure(same(httpServletRequest), any(), exceptionArgumentCaptor.capture());
            Assertions.assertThat(exceptionArgumentCaptor.getValue()).isSameAs(authenticationException);
        }

        public static Stream<Arguments> createExpectedLoginErrorMessageForLoginAttempt() {
            return Stream.of(
                    Arguments.of(Optional.empty(), ErrorMessages.INVALID_USERNAME_OR_PASSWORD),
                    Arguments.of(Optional.of(createLoginAttemptWithCountOfAttempts(MAX_LOGINT_ATTEMPTS - 1)), ErrorMessages.BENUTZER_WIRD_GESPERRT),
                    Arguments.of(Optional.of(createLoginAttemptWithCountOfAttempts(MAX_LOGINT_ATTEMPTS)), ErrorMessages.BENUTZER_WURDE_GESPERRT),
                    Arguments.of(Optional.of(createLoginAttemptWithCountOfAttempts(0)), ErrorMessages.INVALID_USERNAME_OR_PASSWORD));
        }

        public static Stream<Arguments> createExpectedLoginErrorMessageForDisabledExceptionWithMessage() {
            return Stream.of(
                    Arguments.of(new DisabledException(LOGIN_CHECK_MESSAGE), ErrorMessages.NOT_IN_ACTIVE_ELECTION),
                    Arguments.of(new DisabledException("with sth else"), ErrorMessages.INVALID_LOGIN_TIMES));
        }

        private static LoginAttemptModel createLoginAttemptWithCountOfAttempts(int count) {
            return new LoginAttemptModel(UUID.randomUUID(), "", count, LocalDateTime.now());
        }
    }

    private MockHttpServletRequest createAuthenticationRequest(final String username, final String password, final String redirectURL) {
        val httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.setMethod("POST");
        httpServletRequest.setParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        httpServletRequest.setParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY, password);
        val savedRequest = new SimpleSavedRequest(redirectURL);
        httpServletRequest.getSession().setAttribute(SESSION_ATTRIBUTE_SPRING_SECURITY_SAVED_REQUEST, savedRequest);

        return httpServletRequest;
    }

}
