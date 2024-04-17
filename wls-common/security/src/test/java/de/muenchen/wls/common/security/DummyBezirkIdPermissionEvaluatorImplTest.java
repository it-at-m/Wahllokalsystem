package de.muenchen.wls.common.security;
import de.muenchen.wls.common.security.testultils.LoggerExtension;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;

class DummyBezirkIdPermissionEvaluatorImplTest {

    private final DummyBezirkIdPermissionEvaluatorImpl unitUnderTest= new DummyBezirkIdPermissionEvaluatorImpl();

    @Nested
    class TestLoggingEvents {

        @RegisterExtension
        public LoggerExtension loggerExtension = new LoggerExtension();

        @Test
        void logInfoMatch() {
            val map = new HashMap<>();
            map.put("bezirkID", "1234");
            map.put("wahlbezirkID", "1234");
            AuthenticationManager authManager = authentication -> authentication;
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("username", "password");
            authRequest.setDetails(map);
            Authentication auth = authManager.authenticate(authRequest);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            Assertions.assertThat(unitUnderTest.tokenUserBezirkIdMatches("1234", auth)).isTrue();
        }
    }
}
