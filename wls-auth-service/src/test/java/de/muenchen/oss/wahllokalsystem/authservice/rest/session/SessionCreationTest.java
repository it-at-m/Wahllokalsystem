package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
@AutoConfigureMockMvc
@Slf4j
class SessionCreationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate testRestTemplate = new TestRestTemplate();

    private final Connection conn = DriverManager.getConnection("jdbc:h2:mem:wls-auth-service", "sa", "");

    @Autowired
    SessionRegistry sessionRegistry;

    SessionCreationTest() throws SQLException {
    }

    @BeforeEach
    public void setUp() throws SQLException {
        purgeSessions();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        purgeSessions();
    }

    private void purgeSessions() throws SQLException {
        Statement stat = conn.createStatement();
        stat.execute("DELETE SPRING_SESSION_ATTRIBUTES");
        stat.execute("DELETE SPRING_SESSION");
        sessionRegistry.getAllPrincipals().forEach(p -> sessionRegistry.getAllSessions(p, true)
                .forEach(s -> sessionRegistry.removeSessionInformation(s.getSessionId())));
    }

    @Test
    public void should_notFindSessions_when_noSessionsInDB() throws SQLException {
        assertEquals(
                0, SessionTestUtils.getSessionIdsFromDatabase(conn).size());
        assertEquals(
                0, SessionTestUtils.getSessionAttributeBytesFromDb(conn).size());
        assertTrue(sessionRegistry.getAllPrincipals().isEmpty(), "Session registry should be empty");
    }

    @Test
    public void should_createSession_when_callingLoginController() throws SQLException {
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/login"));
        val formLoginResponse = testRestTemplate.exchange(formLoginRequest, String.class);

        Assertions.assertThat(formLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        val sessionIdsFromDB = SessionTestUtils.getSessionIdsFromDatabase(conn);
        assertEquals(1, sessionIdsFromDB.size());
    }

    @Test
    public void should_createCsrfSessionAttribute_when_callingLogin() throws SQLException {
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/login"));
        this.testRestTemplate.exchange(formLoginRequest, String.class);
        val sessionAttributesFromDB = SessionTestUtils.getSessionAttributeBytesFromDb(conn);
        assertEquals(1, sessionAttributesFromDB.size());
        String firstAttributesKey = (String) sessionAttributesFromDB.keySet().toArray()[0];
        Assertions.assertThat(firstAttributesKey).endsWith("CSRF_TOKEN");
    }
}
