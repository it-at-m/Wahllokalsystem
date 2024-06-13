package de.muenchen.oss.wahllokalsystem.wls.common.testing;

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

    public static void runWith(String... authorities) {
        SecurityUtils.runAs(TESTUSER_DEFAULT, TESTUSER_PASSWORD_DEFAULT, authorities);
    }

    /**
     * Creates a stream of Arguments with combination of the required authorities with one authority
     * missing in each combination.<br>
     * The first argument is the
     * array with the subset of authorities and the second argument is the missing authority
     * <div>Example:</div>
     * allRequiredAuthorities = { "auth1", "auth2", "auth3" }<br>
     *
     * Result in following arguments:<br>
     * <ol>
     * <li>Arguments.of({"auth2", "auth3"}, "auth1")</li>
     * <li>Arguments.of({"auth1", "auth3"}, "auth2")</li>
     * <li>Arguments.of({"auth1", "auth2"}, "auth3")</li>
     * </ol>
     *
     * @param allRequiredAuthorities
     * @return
     */
    public static Stream<Arguments> buildArgumentsForMissingAuthoritiesVariations(final String[] allRequiredAuthorities) {
        return Arrays.stream(allRequiredAuthorities)
                .map(authorityToRemove ->
                //remove one authority from all required authorities
                Arguments.of(Arrays.stream(allRequiredAuthorities)
                        .filter(authority -> !authority.equals(authorityToRemove)).toArray(String[]::new), authorityToRemove));
    }
}
