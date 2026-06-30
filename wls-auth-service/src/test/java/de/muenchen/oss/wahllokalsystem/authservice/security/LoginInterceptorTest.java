package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.LegalLoginIntervalModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahlbezirksartModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahltagClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.InfrastrukturelleWlsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

@ExtendWith(MockitoExtension.class)
class LoginInterceptorTest {

  private static final String ADMIN_AUTHORITY = "admin";
  private static final String AUTHORITY_SCHRIFTFUEHRUNG = "schriftfuehrung";

  @Mock LoginTimeClient loginTimeClient;

  @Mock WahltagClient wahltagClient;

  @Mock UserService userService;

  @InjectMocks LoginInterceptor unitUnderTest;

  @Nested
  class ValidateLoginOrThrow {

    @Test
    void should_notThrowException_when_loginTimeIsNotToCheck() {
      val ldapUserDetails = new TestLdapUserDetails("");

      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails));
    }

    @Test
    void should_throwDisabledException_when_userIsNotAssignedToActiveWahltag() {
      val wahltagID = "wahltagID";
      val username = "username";
      val ldapUserDetails = new TestLdapUserDetails(username, AUTHORITY_SCHRIFTFUEHRUNG);

      val mockedUserModelFromUserService = createUserModel(wahltagID, AUTHORITY_SCHRIFTFUEHRUNG);

      Mockito.when(wahltagClient.isWahltagActive(wahltagID)).thenReturn(false);
      Mockito.when(userService.getUser(username))
          .thenReturn(Optional.of(mockedUserModelFromUserService));
      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);

      Assertions.assertThatThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails))
          .isInstanceOf(DisabledException.class);
    }

    @Test
    void should_throwDisabledException_when_userLoginIsAfterLatestAllowedLoginTime() {
      val wahltagID = "wahltagID";
      val username = "username";
      val ldapUserDetails = new TestLdapUserDetails(username, AUTHORITY_SCHRIFTFUEHRUNG);

      val mockedUserModelFromUserService = createUserModel(wahltagID, AUTHORITY_SCHRIFTFUEHRUNG);
      val mockedLegalLoginInterval =
          new LegalLoginIntervalModel(
              LocalDateTime.now().minusYears(1), LocalDateTime.now().minusMinutes(1));

      Mockito.when(wahltagClient.isWahltagActive(wahltagID)).thenReturn(true);
      Mockito.when(userService.getUser(username))
          .thenReturn(Optional.of(mockedUserModelFromUserService));
      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);
      Mockito.when(loginTimeClient.getLegalLoginInterval()).thenReturn(mockedLegalLoginInterval);

      Assertions.assertThatThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails))
          .isInstanceOf(DisabledException.class);
    }

    @Test
    void should_throwDisabledException_when_userLoginIsBeforeEarliestAllowedLoginTime() {
      val wahltagID = "wahltagID";
      val username = "username";
      val ldapUserDetails = new TestLdapUserDetails(username, AUTHORITY_SCHRIFTFUEHRUNG);

      val mockedUserModelFromUserService = createUserModel(wahltagID, AUTHORITY_SCHRIFTFUEHRUNG);
      val mockedLegalLoginInterval =
          new LegalLoginIntervalModel(
              LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusYears(1));

      Mockito.when(wahltagClient.isWahltagActive(wahltagID)).thenReturn(true);
      Mockito.when(userService.getUser(username))
          .thenReturn(Optional.of(mockedUserModelFromUserService));
      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);
      Mockito.when(loginTimeClient.getLegalLoginInterval()).thenReturn(mockedLegalLoginInterval);

      Assertions.assertThatThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails))
          .isInstanceOf(DisabledException.class);
    }

    @Test
    void should_catchWlsException_when_retrievingLegalLoginTimeFails() {
      val wahltagID = "wahltagID";
      val username = "username";
      val ldapUserDetails = new TestLdapUserDetails(username, AUTHORITY_SCHRIFTFUEHRUNG);

      val mockedUserModelFromUserService = createUserModel(wahltagID, AUTHORITY_SCHRIFTFUEHRUNG);
      val mockedLegalLoginClientException =
          InfrastrukturelleWlsException.withCode("")
              .buildWithMessage("getting legal login interval failed");

      Mockito.when(wahltagClient.isWahltagActive(wahltagID)).thenReturn(true);
      Mockito.when(userService.getUser(username))
          .thenReturn(Optional.of(mockedUserModelFromUserService));
      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);
      Mockito.doThrow(mockedLegalLoginClientException)
          .when(loginTimeClient)
          .getLegalLoginInterval();

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails));
    }

    @Test
    void should_notThrowException_when_infomanagementClientThrowsExceptionOnWahltagIsActiveCheck() {
      val wahltagID = "wahltagID";
      val username = "username";
      val ldapUserDetails = new TestLdapUserDetails(username, AUTHORITY_SCHRIFTFUEHRUNG);

      val mockedUserModelFromUserService = createUserModel(wahltagID, AUTHORITY_SCHRIFTFUEHRUNG);
      val mockedWahltagClientIsWahltagActiveException =
          InfrastrukturelleWlsException.withCode("")
              .buildWithMessage("checking wahltag is active failed");
      val mockedLegalLoginInterval =
          new LegalLoginIntervalModel(
              LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

      Mockito.doThrow(mockedWahltagClientIsWahltagActiveException)
          .when(wahltagClient)
          .isWahltagActive(wahltagID);
      Mockito.when(userService.getUser(username))
          .thenReturn(Optional.of(mockedUserModelFromUserService));
      Mockito.when(userService.getSchriftfuehrungAuthorityName())
          .thenReturn(AUTHORITY_SCHRIFTFUEHRUNG);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(ADMIN_AUTHORITY);
      Mockito.when(loginTimeClient.getLegalLoginInterval()).thenReturn(mockedLegalLoginInterval);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.validateLoginOrThrow(ldapUserDetails));
    }

    private UserModel createUserModel(final String wahltagID, final String... authorities) {
      return new UserModel(
          "",
          "",
          true,
          wahltagID,
          LocalDate.now(),
          "",
          "",
          WahlbezirksartModel.UWB,
          "",
          Set.of(authorities),
          "");
    }
  }

  @RequiredArgsConstructor
  static class TestLdapUserDetails implements LdapUserDetails {

    private final String username;
    private final Collection<SimpleGrantedAuthority> authorities;

    public TestLdapUserDetails(final String username, final String... authorities) {
      this.username = username;
      this.authorities = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getDn() {
      return "";
    }

    @Override
    public void eraseCredentials() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    @Override
    public String getPassword() {
      return "";
    }

    @Override
    public String getUsername() {
      return username;
    }
  }
}
