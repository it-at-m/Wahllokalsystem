package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.domain.OAuthServerSessions;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import de.muenchen.oss.wahllokalsystem.authservice.utils.Authorities;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wls-auth-service;DB_CLOSE_ON_EXIT=FALSE",
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

    private Connection conn;

    @Autowired
    SessionRegistry sessionRegistry;

    SessionControllerTest() throws SQLException {
    }

    @Builder
    record ColumnNameContentPair(String columnName, Object columnContent) {
    }

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:wls-auth-service", "sa", "");
        purgeSessions();
        SecurityUtils.runWith(Authorities.ROLE_ADMIN);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        purgeSessions();
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    private void purgeSessions() throws SQLException {
        try (Statement stat = conn.createStatement()) {
            stat.execute("DELETE SPRING_SESSION_ATTRIBUTES");
            stat.execute("DELETE SPRING_SESSION");
        }
        sessionRegistry.getAllPrincipals().forEach(p -> sessionRegistry.getAllSessions(p, true)
                .forEach(s -> sessionRegistry.removeSessionInformation(s.getSessionId())));
    }

    @Test
    public void should_findSessions_when_sessionsCreated() throws Exception {
        val session1 = createSpringSession_inDB_and_inSessionRegistry_forUser(1, "user1");
        val session2 = createSpringSession_inDB_and_inSessionRegistry_forUser(2, "user2");
        val session3 = createSpringSession_inDB_and_inSessionRegistry_forUser(3, "user3");

        val response = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
        val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), OAuthServerSessions.class);
        val foundSessions = responseBody.getSessions();

        Assertions.assertThat(foundSessions).filteredOn(session -> session.getUsername().equals(session1.getPrincipal())
                || session.getUsername().equals(session2.getPrincipal())
                || session.getUsername().equals(session3.getPrincipal())).hasSize(3);
    }

    @Test
    @WithUserDetails
    public void should_killSession_when_killingSessionCalled() throws Exception {
        val session1 = createSpringSession_inDB_and_inSessionRegistry_forUser(1, "user1");
        val session2 = createSpringSession_inDB_and_inSessionRegistry_forUser(2, "user2");
        val session3 = createSpringSession_inDB_and_inSessionRegistry_forUser(3, "user3");

        val response_readSessions = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
        val responseBody = objectMapper.readValue(response_readSessions.getResponse().getContentAsString(), OAuthServerSessions.class);
        val foundSessions = responseBody.getSessions();
        //confirmation sessions are present in sessionRegistry
        Assertions.assertThat(foundSessions).filteredOn(session -> session.getUsername().equals(session1.getPrincipal())
                || session.getUsername().equals(session2.getPrincipal())
                || session.getUsername().equals(session3.getPrincipal())).hasSize(3);
        //confirmation sessions are present in Database
        Assertions.assertThat(SessionTestUtils.getSessionIdsFromDatabase(conn)).filteredOn(sessionId -> sessionId.equals(session1.getSessionId())
                || sessionId.equals(session2.getSessionId())
                || sessionId.equals(session3.getSessionId())).hasSize(3);

        //killing session 2
        val sessionIDToKill = foundSessions.stream().filter(session -> session.getUsername().equals(session2.getPrincipal())).findFirst().get().getSessionId();
        val request_killSession = post("/oauthsessions/" + sessionIDToKill + "/invalidate").with(csrf());
        api.perform(request_killSession).andExpect(status().isOk()).andReturn();

        val response_readSessions_afterKill = api.perform(get("/oauthsessions/")).andExpect(status().isOk()).andReturn();
        val responseBody_afterKill = objectMapper.readValue(response_readSessions_afterKill.getResponse().getContentAsString(), OAuthServerSessions.class);
        val foundSessions_afterKill = responseBody_afterKill.getSessions();
        //session killed in sessionRegistry
        Assertions.assertThat(foundSessions_afterKill).filteredOn(session -> session.getUsername().equals(session1.getPrincipal())
                || session.getUsername().equals(session2.getPrincipal())
                || session.getUsername().equals(session3.getPrincipal())).hasSize(2);
        //session killed in Database
        Assertions.assertThat(SessionTestUtils.getSessionIdsFromDatabase(conn)).filteredOn(sessionId -> sessionId.equals(session1.getSessionId())
                || sessionId.equals(session2.getSessionId())
                || sessionId.equals(session3.getSessionId())).hasSize(2);
    }

    /**
     * Creates a Session for the JDBC SPRING_SESSION table, saves it into table and register it on the
     * sessionRegistry Bean.
     * The manual registration in the Bean is necessary, because with the manual saving in the DB, we
     * are skipping the sessionRegistry Bean and
     * is our responsibility to do this registration manually.
     * The usual way of creating sessions is through calling the Application-Controllers, which should
     * register the session in sessionRegistry
     * and write it into Database - but this has to be tested separately.
     *
     * @param mockSessionNumber - for variations in content of the sessionId and other elements
     * @param username - the principal in the session
     * @return the returned @link{SessionInformation} should be used on assertions and comparisons
     * @throws SQLException - if writing in Database was not properly designed
     */
    public SessionInformation createSpringSession_inDB_and_inSessionRegistry_forUser(int mockSessionNumber, final String username) throws SQLException {
        val timeNow = LocalDateTime.now();
        ZoneOffset zoneOffset = ZoneId.of("Europe/Berlin").getRules().getOffset(timeNow);
        val maxInactiveIntervalMillis = 259200;
        val session_primary_ID = UUID.fromString("0001777" + mockSessionNumber + "-" + "888" + mockSessionNumber + "-" + "888" + mockSessionNumber + "-" + "888"
                + mockSessionNumber + "-" + "11111111888" + mockSessionNumber);
        val sessionID = UUID.fromString("0001888" + mockSessionNumber + "-" + "888" + mockSessionNumber + "-" + "888" + mockSessionNumber + "-" + "888"
                + mockSessionNumber + "-" + "11111111888" + mockSessionNumber);
        val creationTimeSession = timeNow.minusHours(10 - mockSessionNumber);
        val lastAccessTimeSession = creationTimeSession.plusMinutes(10);
        val expiryTimeSession = timeNow.plusMinutes(10);

        List<ColumnNameContentPair> pairs_session = createListColumnNameContentPairs(
                session_primary_ID,
                sessionID,
                creationTimeSession.toInstant(zoneOffset).toEpochMilli(),
                lastAccessTimeSession.toInstant(zoneOffset).toEpochMilli(),
                maxInactiveIntervalMillis,
                expiryTimeSession.toInstant(zoneOffset).toEpochMilli(),
                username);

        val createdDB_Session = writeIntoTable("SPRING_SESSION", pairs_session);
        if (createdDB_Session.getPrincipal() != null && !createdDB_Session.getSessionId().isEmpty()) {
            sessionRegistry.registerNewSession(createdDB_Session.getSessionId(), createdDB_Session.getPrincipal());
            return createdDB_Session;
        } else throw new RuntimeException("create DB-Session error");
    }

    private List<ColumnNameContentPair> createListColumnNameContentPairs(
            final UUID session_primary_ID,
            final UUID sessionID,
            final long creationTime,
            final long lastAccessTime,
            final long maxInactiveInterval,
            final long expiryTime,
            final String principalName) {

        return List.of(
                ColumnNameContentPair.builder().columnName("PRIMARY_ID").columnContent(session_primary_ID).build(),
                ColumnNameContentPair.builder().columnName("SESSION_ID").columnContent(sessionID).build(),
                ColumnNameContentPair.builder().columnName("CREATION_TIME").columnContent(creationTime).build(),
                ColumnNameContentPair.builder().columnName("LAST_ACCESS_TIME").columnContent(lastAccessTime).build(),
                ColumnNameContentPair.builder().columnName("MAX_INACTIVE_INTERVAL").columnContent(maxInactiveInterval).build(),
                ColumnNameContentPair.builder().columnName("EXPIRY_TIME").columnContent(expiryTime).build(),
                ColumnNameContentPair.builder().columnName("PRINCIPAL_NAME").columnContent(principalName).build());
    }

    private SessionInformation writeIntoTable(String tableName, List<ColumnNameContentPair> columnNameContentPairs) throws SQLException {
        StringBuilder columnStatementPart = new StringBuilder();
        StringBuilder valuesStatementPart = new StringBuilder();
        for (int i = 0; i < columnNameContentPairs.size(); i++) {
            if (i == columnNameContentPairs.size() - 1) {
                columnStatementPart.append(columnNameContentPairs.get(i).columnName);
                valuesStatementPart.append("?");
            } else {
                columnStatementPart.append(columnNameContentPairs.get(i).columnName).append(", ");
                valuesStatementPart.append("?,");
            }
        }
        try (PreparedStatement s2 = conn.prepareStatement(
                "INSERT INTO " + tableName + " ("
                        + columnStatementPart
                        + ") VALUES ("
                        + valuesStatementPart
                        + ")")) {
            for (int i = 0; i < columnNameContentPairs.size(); i++) {
                s2.setString((i + 1), columnNameContentPairs.get(i).columnContent.toString());
            }
            s2.addBatch();
            log.info("statement: " + s2);
            s2.executeBatch();
        }
        return new SessionInformation(columnNameContentPairs.get(6).columnContent, columnNameContentPairs.get(1).columnContent.toString(),
                new Date((long) columnNameContentPairs.get(3).columnContent));
    }
}
