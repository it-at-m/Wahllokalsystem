package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.testultils.LoggerExtension;
import java.util.HashMap;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("!dummy.nobezirkid.check")
@ExtendWith(MockitoExtension.class)
class BezirkIDPermissionEvaluatorImplTest {

    private final BezirkIDPermissionEvaluatorImpl unitUnderTest = new BezirkIDPermissionEvaluatorImpl();

    @Mock
    Authentication auth;

    @Nested
    class TestTokenUserBezirkIdMatches {

        @RegisterExtension
        public LoggerExtension loggerExtension = new LoggerExtension();

        @Test
        void warnOnAuthenticationIsNull() {
            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", null)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages()).contains("No authentication object for bezirkId=1234");
        }

        @Test
        void errorWhileChecking() {
            Mockito.when(auth.getPrincipal()).thenReturn("1234");

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages()).contains("Error while checking bezirkId.");
        }

        @Test
        void bezirkIDMatchesFromToken() {
            val map = new HashMap<>();
            map.put("wahlbezirkid_wahlnummer", "1234");
            map.put("wahlbezirkID", "1234");

            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Mockito.when(auth.getDetails()).thenReturn(map);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }

        @Test
        void bezirkIDMatchesFromWahlBezirkID() {
            val map = new HashMap<>();
            map.put("wahlbezirkid_wahlnummer", "1234");
            map.put("wahlbezirkID", "4567");

            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Mockito.when(auth.getDetails()).thenReturn(map);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }

        @Test
        void bezirkIDIsNull() {
            val map = new HashMap<>();
            map.put("wahlbezirkid_wahlnummer", "1234");
            map.put("wahlbezirkID", "1234");

            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Mockito.when(auth.getDetails()).thenReturn(map);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches(null, auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(0);
        }

        @Test
        void bezirkIDDoesNotMatch() {
            val map = new HashMap<>();
            map.put("wahlbezirkid_wahlnummer", null);
            map.put("wahlbezirkID", "1234");

            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Mockito.when(auth.getDetails()).thenReturn(map);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("4567", auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(0);
        }
    }
}
