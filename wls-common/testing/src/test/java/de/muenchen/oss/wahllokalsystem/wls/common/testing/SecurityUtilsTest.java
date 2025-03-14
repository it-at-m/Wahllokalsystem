package de.muenchen.oss.wahllokalsystem.wls.common.testing;

import java.util.Collection;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityUtilsTest {

    @Nested
    class RunAs {

        @Test
        void should_setUserAndPasswortInSecurityContext_when_called() {
            val username = "username";
            val password = "password";

            SecurityUtils.runAs(username, password);

            val currentSecurityContext = SecurityContextHolder.getContext();

            Assertions.assertThat(currentSecurityContext.getAuthentication().getPrincipal()).isEqualTo(username);
            Assertions.assertThat(currentSecurityContext.getAuthentication().getCredentials()).isEqualTo(password);
        }

        @Test
        void should_setAuthoritiesInSecurityContext_when_called() {
            val authority1 = "authority1";
            val authority2 = "authority2";
            val authority3 = "authority3";

            SecurityUtils.runAs("", "", authority1, authority2, authority3);

            @SuppressWarnings(
                "unchecked"
            )
            final Collection<SimpleGrantedAuthority> currentAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
                    .getAuthentication().getAuthorities();

            final SimpleGrantedAuthority[] expectedGrantedAuthorities = { new SimpleGrantedAuthority(authority1),
                    new SimpleGrantedAuthority(authority2), new SimpleGrantedAuthority(authority3) };

            Assertions.assertThat(currentAuthorities).hasSize(3);
            Assertions.assertThat(currentAuthorities).containsExactly(expectedGrantedAuthorities);
        }

        @Test
        void should_throwIllegalArgumentException_when_authoritiyIsNull() {
            final String nullAuthority = null;
            Assertions.assertThatThrownBy(() -> SecurityUtils.runAs("", "", nullAuthority)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwIllegalArgumentException_when_authorityIsBlankString() {
            final String blankAuthority = "   ";
            Assertions.assertThatThrownBy(() -> SecurityUtils.runAs("", "", blankAuthority)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class RunWith {

        @Test
        void should_setDefaultUserInSecurityContext_when_called() {
            SecurityUtils.runWith("authority");

            val currentSecurityContext = SecurityContextHolder.getContext();

            Assertions.assertThat(currentSecurityContext.getAuthentication().getPrincipal()).isEqualTo(SecurityUtils.TESTUSER_DEFAULT);
            Assertions.assertThat(currentSecurityContext.getAuthentication().getCredentials()).isEqualTo(SecurityUtils.TESTUSER_PASSWORD_DEFAULT);
        }

        @Test
        void should_setAuthoritiesInSecurityContext_when_authoritiesAreGiven() {
            val authority1 = "authority1";
            val authority2 = "authority2";
            val authority3 = "authority3";

            SecurityUtils.runWith(authority1, authority2, authority3);

            @SuppressWarnings(
                "unchecked"
            )
            final Collection<SimpleGrantedAuthority> currentAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
                    .getAuthentication().getAuthorities();

            final SimpleGrantedAuthority[] expectedGrantedAuthorities = { new SimpleGrantedAuthority(authority1),
                    new SimpleGrantedAuthority(authority2), new SimpleGrantedAuthority(authority3) };

            Assertions.assertThat(currentAuthorities).hasSize(3);
            Assertions.assertThat(currentAuthorities).containsExactly(expectedGrantedAuthorities);
        }

        @Test
        void should_throwIllegalArgumentException_when_authorityIsNull() {
            final String nullAuthority = null;
            Assertions.assertThatThrownBy(() -> SecurityUtils.runWith(nullAuthority)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwIllegalArgumentException_when_authorityIsBlank() {
            final String blankAuthority = "   ";
            Assertions.assertThatThrownBy(() -> SecurityUtils.runWith(blankAuthority)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class BuildArgumentsForMissingAuthoritiesVariations {

        @Test
        void should_createStreamWithCombinationsOfAuthoritiesWithOneAuthorityMissingInEachCombination_when_authoritiesAreGiven() {
            val authority1 = "authority1";
            val authority2 = "authority2";
            val authority3 = "authority3";

            val argumentList = SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(new String[] { authority1, authority2, authority3 }).toList();

            Assertions.assertThat(argumentList).hasSize(3);
            Assertions.assertThat(argumentList.get(0).get()).isEqualTo(new Object[] { new String[] { authority2, authority3 }, authority1 });
            Assertions.assertThat(argumentList).map(Arguments::get)
                    .contains(createExpectedArgumentBody(authority1, authority2, authority3), createExpectedArgumentBody(authority2, authority1, authority3),
                            createExpectedArgumentBody(authority3, authority1, authority2));
        }

        private Object[] createExpectedArgumentBody(final String title, final String... authorities) {
            return new Object[] { authorities, title };
        }
    }

}
