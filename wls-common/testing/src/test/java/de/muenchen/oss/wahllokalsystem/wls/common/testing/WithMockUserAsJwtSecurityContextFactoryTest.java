package de.muenchen.oss.wahllokalsystem.wls.common.testing;

import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class WithMockUserAsJwtSecurityContextFactoryTest {

    private final WithMockUserAsJwtSecurityContextFactory unitUnderTest = new WithMockUserAsJwtSecurityContextFactory();

    @Nested
    class CreateSecurityContext {

        @Test
        void should_createSecurityContextWithAuthorities_when_authoritiesAreGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithAuthoritiesInAnnotations.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val authorities = jwtAuthentication.getAuthorities();

            val expectedAuthorities = List.of(new SimpleGrantedAuthority("Authority1"), new SimpleGrantedAuthority("Authority2"));

            Assertions.assertThat(authorities).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedAuthorities);
        }

        @Test
        void should_createSecurityContextWithEmptyAuthoritiesList_when_noAuthoritiesAreGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithNoPropertiesSetInTheAnnotation.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val authorities = jwtAuthentication.getAuthorities();

            Assertions.assertThat(authorities).isEmpty();
        }

        @Test
        void should_setUsernameInJwtToken_when_usernameIsGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithUsernameInAnnotation.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val username = jwtAuthentication.getToken().getTokenValue();

            Assertions.assertThat(username).isEqualTo("username");
        }

        @Test
        void should_hasDefaultUsername_when_usernameIsNotGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithNoPropertiesSetInTheAnnotation.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val username = jwtAuthentication.getToken().getTokenValue();

            Assertions.assertThat(username).isEqualTo("user");
        }

        @Test
        void should_addClaimWithDefaultKeyValueSeparator_when_claimsAreGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithClaimsInAnnotation.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val claims = jwtAuthentication.getToken().getClaims();

            Assertions.assertThat(claims).contains(Map.entry("key1", "value1"));
            Assertions.assertThat(claims).contains(Map.entry("key2", "value2"));
        }

        @Test
        void should_addClaimByDefineKeyValueSeparator_when_claimsAndSeparatorAreGivenInTheAnnotation() {
            val securityContext = unitUnderTest.createSecurityContext(getAnnotation(TestClassWithClaimsAndClaimSeparatorInAnnotation.class));

            val jwtAuthentication = (JwtAuthenticationToken) securityContext.getAuthentication();
            val claims = jwtAuthentication.getToken().getClaims();

            Assertions.assertThat(claims).contains(Map.entry("key1", "value1"));
            Assertions.assertThat(claims).contains(Map.entry("key2", "value2"));
        }

        private WithMockUserAsJwt getAnnotation(Class<?> clazz) {
            return clazz.getAnnotation(WithMockUserAsJwt.class);
        }

    }

}

@WithMockUserAsJwt
class TestClassWithNoPropertiesSetInTheAnnotation {

}

@WithMockUserAsJwt(authorities = { "Authority1", "Authority2" })
class TestClassWithAuthoritiesInAnnotations {
}

@WithMockUserAsJwt(value = "username")
class TestClassWithUsernameInAnnotation {
}

@WithMockUserAsJwt(claimProperties = { "key1=value1", "key2=value2" })
class TestClassWithClaimsInAnnotation {

}

@WithMockUserAsJwt(claimProperties = { "key1;value1", "key2;value2" }, claimPropertiesSeparator = ";")
class TestClassWithClaimsAndClaimSeparatorInAnnotation {

}
