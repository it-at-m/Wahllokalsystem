package de.muenchen.oss.wahllokalsystem.broadcastservice.security;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastService;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.BroadcastSecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
@Slf4j
public class BroadcastSecurityTest {

    @Autowired
    BroadcastService broadcastService;

    @BeforeEach
    void setUp() {
        Assertions.assertThat(broadcastService).isNotNull();
        SecurityContextHolder.clearContext();
    }

    @Nested
    class BroadcastTest {

        @Test
        void accessDenied_dummyRolle() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD,
                            AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.broadcast(null)).withMessageStartingWith("Access Denied");
        }

        @Test
        void accessDenied_missingOneAuthority_BROADCAST_WRITE_MESSAGE() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST
            });

            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should fail");
            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Access Denied");

        }

        @Test
        void accessDenied_missingOneAuthority_BROADCAST_BUSINESSACTION_BROADCAST() {

            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should fail");
            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_WRITE_MESSAGE
            });

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Access Denied");

        }

        @Test
        void accessPositive() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST,
                    BroadcastSecurityUtils.BROADCAST_WRITE_MESSAGE
            });
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should have access");

            Assertions.assertThatCode(() -> broadcastService.broadcast(m1))
                    .doesNotThrowAnyException();
        }

    }

    @Nested
    class GetMessageTest {

        @Test
        void accessDenied() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD,
                            AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.getOldestMessage(null))
                    .withMessageStartingWith("Access Denied");
        }

        @Test
        void accessDenied_missing_Role_BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_BROADCAST
            });

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.getOldestMessage("wahlbezirkId"))
                    .withMessageStartingWith("Access Denied");
        }

        @Test
        void accessPositiveTest_Role_BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN, BroadcastSecurityUtils.BROADCAST_READ_MESSAGE
            });

            Assertions.assertThatThrownBy(() -> broadcastService.getOldestMessage("wahlbezirkId")).isNotInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void accessDenied() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(TestConstants.TESTUSER, TestConstants.TESTPASSWORD,
                            AuthorityUtils.createAuthorityList("ROLE_DUMMY")));

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage(null))
                    .withMessageStartingWith("Access Denied");
        }

        @Test
        void accessDenied_missing_Role_BROADCAST_DELETE_MESSAGE() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTGELESEN
            });

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage("1-2-3-4-5"))
                    .withMessageStartingWith("Access Denied");

        }

        @Test
        void accessDenied_missing_Role_BROADCAST_BUSINESSACTION_NACHRICHTGELESEN() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_DELETE_MESSAGE
            });

            Assertions.assertThatExceptionOfType(AccessDeniedException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage("1-2-3-4-5"))
                    .withMessageStartingWith("Access Denied");

        }

        @Test
        void accessPositive() {

            BroadcastSecurityUtils.runAs(TestConstants.TESTUSER, TestConstants.TESTPASSWORD, new String[] {
                    BroadcastSecurityUtils.BROADCAST_BUSINESSACTION_NACHRICHTGELESEN,
                    BroadcastSecurityUtils.BROADCAST_DELETE_MESSAGE
            });

            Assertions.assertThatCode(() -> broadcastService.deleteMessage("1-2-3-4-5"))
                    .doesNotThrowAnyException();
        }

    }

}
