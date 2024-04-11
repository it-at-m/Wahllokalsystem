package de.muenchen.wls.common.wls.security;

import de.muenchen.wls.common.security.BezirkIDPermissionEvaluatorImpl;
import de.muenchen.wls.common.wls.security.testultils.LoggerExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class BezirkIDPermissionEvaluatorImplTest {

    private final BezirkIDPermissionEvaluatorImpl unitUnderTest= new BezirkIDPermissionEvaluatorImpl();

    @Nested
    class TestLoggingEvents {

        @RegisterExtension
        public LoggerExtension loggerExtension = new LoggerExtension();

        @Test
        void warnOnAuthenticationIsNull() {
            unitUnderTest.tokenUserBezirkIdMatches("1234",null);
            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
        }
    }
}
