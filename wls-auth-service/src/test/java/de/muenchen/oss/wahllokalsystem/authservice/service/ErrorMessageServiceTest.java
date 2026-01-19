package de.muenchen.oss.wahllokalsystem.authservice.service;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

class ErrorMessageServiceTest {

  private final ErrorMessageService unitUnderTest = new ErrorMessageService();

  @Nested
  class getErrorMessage {

    @Test
    void should_returnTheMessageOfTheException_when_exceptionIsInstanceOfBadCredentialsException() {
      val errorMessage = "bad credentials";
      val result = unitUnderTest.getErrorMessage(new BadCredentialsException(errorMessage));

      Assertions.assertThat(result).isEqualTo(errorMessage);
    }

    @Test
    void should_returnTheMessageOfTheException_when_exceptionIsInstanceOfLockedException() {
      val errorMessage = "bad credentials";
      val result = unitUnderTest.getErrorMessage(new LockedException(errorMessage));

      Assertions.assertThat(result).isEqualTo(errorMessage);
    }

    @Test
    void should_returnTheMessageOfTheException_when_exceptionIsAccessDeniedException() {
      val errorMessage = "bad credentials";
      val result = unitUnderTest.getErrorMessage(new AccessDeniedException(errorMessage));

      Assertions.assertThat(result).isEqualTo(errorMessage);
    }

    @Test
    void
        should_returnTheMessageOfTheExceptionAfterFixedErrorMessage_when_exceptionIsInstanceOfSessionAuthenticationException() {
      val errorMessage = "bad credentials";
      val result = unitUnderTest.getErrorMessage(new SessionAuthenticationException(errorMessage));

      Assertions.assertThat(result)
          .isEqualTo(ErrorMessageService.BENUTZER_BEREITS_ANGEMELDET + errorMessage);
    }

    @Test
    void
        should_returnFixedErrorMessage_when_exceptionIsInstanceOfInternalAuthenticationServiceException() {
      val errorMessage = "bad credentials";
      val result =
          unitUnderTest.getErrorMessage(new InternalAuthenticationServiceException(errorMessage));

      Assertions.assertThat(result).isEqualTo(ErrorMessageService.LOGIN_BLOCKED_BY_LDAP_SERVER);
    }

    @Test
    void should_returnFixedErrorMessage_when_exceptionIsNotHandledBefore() {
      val errorMessage = "bad credentials";
      val result = unitUnderTest.getErrorMessage(new Exception(errorMessage));

      Assertions.assertThat(result).isEqualTo(ErrorMessageService.INVALID_USERNAME_OR_PASSWORD);
    }
  }
}
