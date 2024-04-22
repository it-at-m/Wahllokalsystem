package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.testultils.LoggerExtension;
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
class DummyBezirkIdPermissionEvaluatorImplTest {

    @Mock
    Authentication auth;

    private final DummyBezirkIdPermissionEvaluatorImpl unitUnderTest = new DummyBezirkIdPermissionEvaluatorImpl();

    @Nested
    class TestLoggingEvents {

        @RegisterExtension
        public LoggerExtension loggerExtension = new LoggerExtension();

        @Test
        void logInfoMatch() {
            Mockito.when(auth.getPrincipal()).thenReturn("1234");
            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }
    }
}
