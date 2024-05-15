package de.muenchen.oss.wahllokalsystem.infomanagementservice.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static final String TESTUSER_DEFAULT = "TESTUSER";

    public static final String TESTUSER_PASSWORD_DEFUALT = "TESTUSER_PASSWORD";

    public static void runWith(String... authorities) {
        SecurityUtils.runAs(TESTUSER_DEFAULT, TESTUSER_PASSWORD_DEFUALT, authorities);
    }

    public static void runAs(String username, String password, String... authorities) {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(authorities)));
    }

}
