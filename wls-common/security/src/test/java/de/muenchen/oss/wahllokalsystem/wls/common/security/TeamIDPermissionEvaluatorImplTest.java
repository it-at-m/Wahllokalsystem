package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

class TeamIDPermissionEvaluatorImplTest {

  AuthDetailRetriever authDetailRetriever = Mockito.mock(AuthDetailRetriever.class);

  private final TeamIDPermissionEvaluatorImpl unitUnderTest =
      new TeamIDPermissionEvaluatorImpl(List.of(authDetailRetriever));

  @Mock Authentication auth;

  @Nested
  class TokenUserteamIdMatches {

    @Test
    void should_returnFalse_when_teamIDIsNull() {
      Assertions.assertThat(
              unitUnderTest.tokenUserteamIdMatches(null, Mockito.mock(Authentication.class)))
          .isFalse();
    }

    @Test
    void should_returnFalse_when_authenticationIsNull() {
      Assertions.assertThat(unitUnderTest.tokenUserteamIdMatches("B", auth)).isFalse();
    }

    @Test
    void should_returnFalse_when_hasNoAuthDetailsHandlers() {
      val unitUnderTest = new TeamIDPermissionEvaluatorImpl(Collections.emptyList());
      Assertions.assertThat(
              unitUnderTest.tokenUserteamIdMatches("B", Mockito.mock(Authentication.class)))
          .isFalse();
    }

    @Test
    void should_returnFalse_when_noAuthDetailsHandlerCanHandleAuthentication() {
      val requiredTeamID = "B";
      val authentication = Mockito.mock(Authentication.class);

      Mockito.when(authDetailRetriever.canHandle(authentication)).thenReturn(false);

      Assertions.assertThat(unitUnderTest.tokenUserteamIdMatches(requiredTeamID, authentication))
          .isFalse();
    }

    @Test
    void should_returnFalse_when_authenticationHasNoTeamID() {
      val requiredTeamID = "B";
      val authentication = Mockito.mock(Authentication.class);

      Mockito.when(authDetailRetriever.canHandle(authentication)).thenReturn(true);
      Mockito.when(authDetailRetriever.getDetail(Mockito.any(), Mockito.eq(authentication)))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.tokenUserteamIdMatches(requiredTeamID, authentication);
      Assertions.assertThat(result).isFalse();
    }

    @Test
    void should_returnFalse_when_requiredTeamIDDoesNotMatchWithAuthentication() {
      val requiredTeamID = "B";
      val authentication = Mockito.mock(Authentication.class);

      Mockito.when(authDetailRetriever.canHandle(authentication)).thenReturn(true);
      Mockito.when(authDetailRetriever.getDetail("teamID", authentication))
          .thenReturn(Optional.of(requiredTeamID + "sth"));

      val result = unitUnderTest.tokenUserteamIdMatches(requiredTeamID, authentication);
      Assertions.assertThat(result).isFalse();
    }

    @Test
    void should_returnTrue_when_requiredTeamIDMatchesWithAuthentication() {
      val requiredTeamID = "B";
      val authentication = Mockito.mock(Authentication.class);

      Mockito.when(authDetailRetriever.canHandle(authentication)).thenReturn(true);
      Mockito.when(authDetailRetriever.getDetail("teamID", authentication))
          .thenReturn(Optional.of(requiredTeamID));

      val result = unitUnderTest.tokenUserteamIdMatches(requiredTeamID, authentication);
      Assertions.assertThat(result).isTrue();
    }
  }
}
