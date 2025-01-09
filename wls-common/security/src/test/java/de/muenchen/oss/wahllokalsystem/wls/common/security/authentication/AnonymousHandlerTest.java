package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class AnonymousHandlerTest {

    private final AnonymousHandler unitUnderTest = new AnonymousHandler();

    @Nested
    class CanHandle {

        @Test
        void should_returnTrue_when_authenticationIsAnonymousAuthenticationToken() {
            Assertions.assertThat(unitUnderTest.canHandle(new AnonymousAuthenticationToken("key", "principal", List.of(new SimpleGrantedAuthority("role")))))
                    .isTrue();
        }

        @Test
        void should_returnTrue_when_authenticationSubclassOfJwtAuthenticationToken() {
            Assertions.assertThat(unitUnderTest.canHandle(new AnonymousAuthenticationToken("key", "principal", List.of(new SimpleGrantedAuthority("role"))) {
            })).isTrue();
        }

        @Test
        void should_returnFalse_when_authenticationIsNotJwtAuthenticationToken() {
            Assertions.assertThat(unitUnderTest.canHandle(new AbstractAuthenticationToken(List.of(new SimpleGrantedAuthority("role"))) {
                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }
            })).isFalse();
        }
    }

    @Nested
    class GetDetail {

        @Test
        void should_returnEmptyOptional_when_called() {
            val authentication = new AnonymousAuthenticationToken("key", "principal", List.of(new SimpleGrantedAuthority("role")));

            val result = unitUnderTest.getDetail("key", authentication);

            Assertions.assertThat(result).isEmpty();
        }
    }

}
