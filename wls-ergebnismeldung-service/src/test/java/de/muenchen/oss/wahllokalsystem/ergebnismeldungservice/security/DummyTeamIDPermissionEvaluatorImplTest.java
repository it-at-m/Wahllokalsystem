package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.security;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DummyTeamIDPermissionEvaluatorImplTest {

  DummyTeamIDPermissionEvaluatorImpl unitUnderTest = new DummyTeamIDPermissionEvaluatorImpl();

  @Nested
  class TokenUserteamIdMatches {

    @Test
    void should_returnTrue_when_called() {
      Assertions.assertThat(
              unitUnderTest.tokenUserteamIdMatches(Instancio.create(String.class), null))
          .isTrue();
    }
  }
}
