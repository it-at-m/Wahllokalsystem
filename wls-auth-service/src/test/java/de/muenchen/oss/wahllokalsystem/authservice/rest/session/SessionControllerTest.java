package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.rest.OAuthServerSessions;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import de.muenchen.oss.wahllokalsystem.authservice.utils.Authorities;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
@AutoConfigureMockMvc
@Slf4j
class SessionControllerTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        purgeSessions();
        SecurityUtils.runWith(Authorities.ROLE_ADMIN);
    }

    @AfterEach
    void tearDown() {
        purgeSessions();
    }

    private void purgeSessions() {
        sessionRegistry.getAllPrincipals().forEach(p -> sessionRegistry.getAllSessions(p, true)
                .forEach(s -> sessionRegistry.removeSessionInformation(s.getSessionId())));
    }

    @Nested
    class ListActiveSessions {

        @Test
        void should_findSessions_when_sessionsCreated() throws Exception {
            val session1 = createSpringSessionForUser("user1");
            val session2 = createSpringSessionForUser("user2");
            val session3 = createSpringSessionForUser("user3");

            val response = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), OAuthServerSessions.class);
            val foundSessions = responseBody.getSessions();

            Assertions.assertThat(foundSessions).filteredOn(session -> session.getUsername().equals(session1.getAttribute("PRINCIPAL_NAME"))
                    || session.getUsername().equals(session2.getAttribute("PRINCIPAL_NAME"))
                    || session.getUsername().equals(session3.getAttribute("PRINCIPAL_NAME"))).hasSize(3);
        }
    }

    @Nested
    class KillSession {

        @Test
        void should_killSession_when_killingSessionCalled() throws Exception {
            val session1 = createSpringSessionForUser("user1");
            val session2 = createSpringSessionForUser("user2");
            val session3 = createSpringSessionForUser("user3");

            val responseReadSessions = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(responseReadSessions.getResponse().getContentAsString(), OAuthServerSessions.class);
            val foundSessions = responseBody.getSessions();
            //killing session 2
            val sessionIDToKill = foundSessions.stream().filter(session -> session.getUsername().equals(session2.getAttribute("PRINCIPAL_NAME"))).findFirst().get().getSessionId();
            val requestKillSession = post("/oauthsessions/" + sessionIDToKill + "/invalidate").with(csrf());
            api.perform(requestKillSession).andExpect(status().isOk()).andReturn();

            val responseReadSessionsAfterKill = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
            val responseBodyAfterKill = objectMapper.readValue(responseReadSessionsAfterKill.getResponse().getContentAsString(), OAuthServerSessions.class);
            val foundSessionsAfterKill = responseBodyAfterKill.getSessions();
            //session killed in sessionRegistry
            Assertions.assertThat(foundSessionsAfterKill).filteredOn(session -> session.getUsername().equals(session1.getAttribute("PRINCIPAL_NAME"))
                    || session.getUsername().equals(session2.getAttribute("PRINCIPAL_NAME"))
                    || session.getUsername().equals(session3.getAttribute("PRINCIPAL_NAME"))).hasSize(2);
        }
    }

    private Session createSpringSessionForUser(final String username) {
        val timeNow = LocalDateTime.now();
        ZoneOffset zoneOffset = ZoneId.of("Europe/Berlin").getRules().getOffset(timeNow);
        val sessionToSave = sessionRepository.createSession();
        sessionToSave.setLastAccessedTime(timeNow.minusHours(10).toInstant(zoneOffset));
        sessionToSave.setAttribute("PRINCIPAL_NAME", username);
        if (sessionToSave.getAttribute("PRINCIPAL_NAME") != null && !sessionToSave.getId().isEmpty()) {
            sessionRegistry.registerNewSession(sessionToSave.getId(), sessionToSave.getAttribute("PRINCIPAL_NAME"));
            return sessionToSave;
        } else throw new RuntimeException("Create Session error");
    }
}
