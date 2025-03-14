package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.LoggerExtension;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class BezirkIDPermissionEvaluatorImplTest {

    AuthDetailRetriever authDetailRetriever = Mockito.mock(AuthDetailRetriever.class);

    private final BezirkIDPermissionEvaluatorImpl unitUnderTest = new BezirkIDPermissionEvaluatorImpl(List.of(authDetailRetriever));

    @Mock
    Authentication auth;

    @Nested
    class TokenUserBezirkIdMatches {

        @RegisterExtension
        public LoggerExtension loggerExtension = new LoggerExtension();

        @Test
        void should_returnFalseAndCreateLogWarning_when_authenticationIsNull() {
            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", null)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages()).contains("No authentication object for bezirkId=1234");
        }

        @Test
        void should_returnFalseAndCreateLogError_when_errorWhileCheckingOccurred() {
            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Mockito.doThrow(new RuntimeException("error")).when(authDetailRetriever).canHandle(auth);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages()).contains("Error while checking bezirkId.");
        }

        @Test
        void should_returnTrue_when_bezirkIDMatchesFromToken() {
            val wahlbezirkID = "1234";

            Mockito.when(authDetailRetriever.canHandle(auth)).thenReturn(true);
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkID", auth)).thenReturn(Optional.of(wahlbezirkID));
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkid_wahlnummer", auth)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }

        @Test
        void should_returnTrue_when_bezirkIDMatchesFromWahlBezirkID() {
            val wahlbezirkid_wahlnummer = "1234";

            Mockito.when(authDetailRetriever.canHandle(auth)).thenReturn(true);
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkID", auth)).thenReturn(Optional.empty());
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkid_wahlnummer", auth)).thenReturn(Optional.of(wahlbezirkid_wahlnummer));

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }

        @Test
        void should_returnFalse_when_bezirkIDIsNull() {
            val wahlbezirkID = "1234";

            Mockito.when(authDetailRetriever.canHandle(auth)).thenReturn(true);
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkID", auth)).thenReturn(Optional.empty());
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkid_wahlnummer", auth)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches(null, auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(0);
        }

        @Test
        void should_returnFalse_when_bezirkIDDoesNotMatch() {
            Mockito.when(authDetailRetriever.canHandle(auth)).thenReturn(true);
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkID", auth)).thenReturn(Optional.empty());
            Mockito.when(authDetailRetriever.getDetail("wahlbezirkid_wahlnummer", auth)).thenReturn(Optional.of("1234"));

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("4567", auth)).isFalse();
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(0);
        }

        @Test
        void should_returnFalse_when_noHandlerForAuthenticationIsGiven() {
            Mockito.when(authDetailRetriever.canHandle(auth)).thenReturn(false);

            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isFalse();
        }
    }
}
