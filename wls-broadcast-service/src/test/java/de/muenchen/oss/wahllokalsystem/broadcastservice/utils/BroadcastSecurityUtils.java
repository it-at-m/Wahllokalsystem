package de.muenchen.oss.wahllokalsystem.broadcastservice.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class BroadcastSecurityUtils {

    public static String BROADCAST_BUSINESSACTION_BROADCAST = "Broadcast_BUSINESSACTION_Broadcast";
    public static String BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN = "Broadcast_BUSINESSACTION_GetMessage";
    public static String BROADCAST_BUSINESSACTION_NACHRICHTGELESEN = "Broadcast_BUSINESSACTION_MessageRead";

    public static String BROADCAST_READ_MESSAGE = "Broadcast_READ_Message";
    public static String BROADCAST_WRITE_MESSAGE = "Broadcast_WRITE_Message";
    public static String BROADCAST_DELETE_MESSAGE = "Broadcast_DELETE_Message";

    private static final String TESTUSER = "redv-101";
    private static final String TESTPASSWORD = "password";

    /**
     * Gewährung eines Vollzugriffs durch Zuteilung von allen möglichen Rollen.
     */
    public static void grantFullAccess() {
        runAs(TESTUSER, TESTPASSWORD, getAllAuthorities());
    }

    public static void runAs(String username, String password, String... roles) {
        assertNotNull(username, "Username must not be null!");
        assertNotNull(password, "Password must not be null!");
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(roles)));
    }

    public static String[] getAllAuthorities() {
        return new String[] {
                BROADCAST_BUSINESSACTION_BROADCAST,
                BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN,
                BROADCAST_BUSINESSACTION_NACHRICHTGELESEN,

                BROADCAST_READ_MESSAGE,
                BROADCAST_WRITE_MESSAGE,
                BROADCAST_DELETE_MESSAGE
        };
    }
}
