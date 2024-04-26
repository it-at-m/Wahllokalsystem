package de.muenchen.oss.wahllokalsystem.broadcastservice.security;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastService;

import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.BroadcastSecurityUtils;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
@Slf4j
public class BroadcastSecurityTest {

    @Autowired
    BroadcastService broadcastService;

    @BeforeEach
    public void setUp() {
        Assertions.assertThat(broadcastService).isNotNull();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void broadcastAccessDeniedTest() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.broadcast(null)).withMessageStartingWith("Access Denied");
    }

    @Test
    public void getMessageAccessDeniedTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.getOldestMessage(null))
                .withMessageStartingWith("Access Denied");
    }

    @Test
    public void deleteAccessDeniedTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.deleteMessage(null))
                .withMessageStartingWith("Access Denied");
    }

    @Test
    public void broadcastAccessPositiveTest() {

        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST,
                BroadcastSecurityUtils.BROADCAST_WRITE_MESSAGE
        });

        Exception thrownException = null;
        try {
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should have access");
            broadcastService.broadcast(m1);
        } catch (Exception e) {
            thrownException = e;
        }

        Assertions.assertThat(thrownException).isNull();

        /**
         * Fails if missing authority BroadcastSecurityUtils.BROADCAST_WRITE_MESSAGE
         */
        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST
        });

        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
        BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should fail");
        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.broadcast(m1))
                .withMessageStartingWith("Access Denied");

        /**
         * Fails if missing authority BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST
         */
        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_WRITE_MESSAGE
        });

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.broadcast(m1))
                .withMessageStartingWith("Access Denied");
    }

    @Test
    public void getMessageAccessPositiveTest() {

        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN
        });

        Exception thrownException = null;
        try {
            broadcastService.getOldestMessage("wahlbezirkId");
        } catch (Exception e) {
            thrownException = e;
        }

        Assertions.assertThat(thrownException).isNotInstanceOf(AccessDeniedException.class);

        /**
         * Fails if missing authority BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN
         */
        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST
        });

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.getOldestMessage("wahlbezirkId"))
                .withMessageStartingWith("Access Denied");
    }

    @Test
    public void deleteMessageAccessPositiveTest() {

        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTGELESEN,
                BroadcastSecurityUtils.BROADCAST_DELETE_MESSAGE
        });

        Exception thrownException = null;
        try {
            broadcastService.deleteMessage("1-2-3-4-5");
        } catch (Exception e) {
            thrownException = e;
        }

        Assertions.assertThat(thrownException).isNull();

        /**
         * Fails if missing authority BroadcastSecurityUtils.BROADCAST_DELETE_MESSAGE
         */
        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTGELESEN
        });

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.deleteMessage("1-2-3-4-5"))
                .withMessageStartingWith("Access Denied");

        /**
         * Fails if missing authority BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTGELESEN
         */
        BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                BroadcastSecurityUtils.BROADCAST_DELETE_MESSAGE
        });

        Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> broadcastService.deleteMessage("1-2-3-4-5"))
                .withMessageStartingWith("Access Denied");
    }

}
