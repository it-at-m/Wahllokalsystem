package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static final String TESTUSER_DEFAULT = "TESTUSER";

    public static final String TESTUSER_PASSWORD_DEFAULT = "TESTUSER_PASSWORD";

    public static void runAs(String username, String password, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(authorities)));
    }

    public static void runAs(String... authorities) {
        SecurityUtils.runAs(TESTUSER_DEFAULT, TESTUSER_PASSWORD_DEFAULT, authorities);
    }

    public static Stream<Arguments> buildArgumentsForMissingAuthoritiesVariations(final String[] allRequiredAuthorities) {
        return Arrays.stream(allRequiredAuthorities)
            .map(authorityToRemove ->
                //remove one authority from all required authorities
                Arguments.of(Arrays.stream(allRequiredAuthorities)
                    .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
    }
}
