package de.muenchen.oss.wahllokalsystem.wls.common.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class DummyTeamIDPermissionEvaluatorImplTest {

  @Mock Authentication auth;

  private final DummyTeamIDPermissionEvaluatorImpl unitUnderTest =
      new DummyTeamIDPermissionEvaluatorImpl();

  @Nested
  class TokenUserteamIdMatches {

    @Test
    void should_returnTrue_when_called() {
      Assertions.assertThat(unitUnderTest.tokenUserteamIdMatches("B", auth)).isTrue();
    }
  }
}
