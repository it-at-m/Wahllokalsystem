package de.muenchen.oss.wahllokalsystem.security;

import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_TEST_PROFILE;
import de.muenchen.oss.wahllokalsystem.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.TestConstants;
import de.muenchen.oss.wahllokalsystem.service.BroadcastService;
import de.muenchen.oss.wahllokalsystem.utils.UsernamePasswordAuthenticationToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wahllokalsystem;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0",
                "server.port=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class BroadcastSecurityTest {

    @Autowired
    BroadcastService broadcast_S;

    @BeforeEach
    public void setUp() {
        Assertions.assertThat(broadcast_S).isNotNull();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void broadcastAccessDeniedTest() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));
        Exception thrownException = null;
        try {
            //noinspection DataFlowIssue
            broadcast_S.broadcast(null);
        } catch (Exception e) {
            thrownException = e;
        }
        Assertions.assertThat(thrownException)
                .isNotNull()
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageStartingWith("Access Denied");
    }

    @Test
    public void getMessageAccessDeniedTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));
        RuntimeException thrownException = null;
        try {
            broadcast_S.getOldestMessage(null);
        } catch (Exception e) {
            thrownException = (RuntimeException) e;
        }
        Assertions.assertThat(thrownException)
                .isNotNull()
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageStartingWith("Access Denied");
    }

    @Test
    public void deleteAccessDeniedTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));
        RuntimeException thrownException = null;
        try {
            broadcast_S.deleteMessage(null);
        } catch (Exception e) {
            thrownException = (RuntimeException) e;
        }
        Assertions.assertThat(thrownException)
                .isNotNull()
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageStartingWith("Access Denied");
    }

}
