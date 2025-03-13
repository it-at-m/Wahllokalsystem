package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;

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
import org.springframework.beans.factory.annotation.Value;
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

    private static final String LOGIN_ENDPOINT = "/login";
    private static final String TABLE_SESSION_ATTRIBUTES = "SPRING_SESSION_ATTRIBUTES";
    private static final String TABLE_SESSIONS = "SPRING_SESSION";

    @LocalServerPort
    private int port;

    @Value("${spring.datasource.url}")
    String dataSourceUrl;

    @Value("${spring.datasource.username}")
    String dataSourceUsername;

    @Value("${spring.datasource.password}")
    String dataSourcePassword;

    @Autowired
    TestRestTemplate testRestTemplate;

    private Connection conn;

    @Autowired
    SessionRegistry sessionRegistry;

    @BeforeEach
    public void setup() throws SQLException {
        conn = DriverManager.getConnection(dataSourceUrl, dataSourceUsername, dataSourcePassword);
        purgeSessions();
    }

    @AfterEach
    public void teardown() throws SQLException {
        purgeSessions();
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    private void purgeSessions() throws SQLException {
        try (Statement stat = conn.createStatement()) {
            stat.execute("DELETE " + TABLE_SESSION_ATTRIBUTES);
            stat.execute("DELETE " + TABLE_SESSIONS);
        }
        sessionRegistry.getAllPrincipals().forEach(p -> sessionRegistry.getAllSessions(p, true)
                .forEach(s -> sessionRegistry.removeSessionInformation(s.getSessionId())));
    }

    @Test
    void should_notFindSessions_when_noSessionsInDB() throws SQLException {
        Assertions.assertThat(SessionTestUtils.getSessionIdsFromDatabase(conn).size()).isEqualTo(0);
        Assertions.assertThat(SessionTestUtils.getSessionAttributeBytesFromDb(conn).size()).isEqualTo(0);
        Assertions.assertThat(sessionRegistry.getAllPrincipals().isEmpty()).isTrue();
    }

    @Test
    void should_createSession_when_callingLoginController() throws SQLException {
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + LOGIN_ENDPOINT));
        val formLoginResponse = testRestTemplate.exchange(formLoginRequest, String.class);

        Assertions.assertThat(formLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        val sessionIdsFromDB = SessionTestUtils.getSessionIdsFromDatabase(conn);
        Assertions.assertThat(sessionIdsFromDB.size()).isEqualTo(1);
    }

    @Test
    void should_createCsrfSessionAttribute_when_callingLogin() throws SQLException {
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + LOGIN_ENDPOINT));
        this.testRestTemplate.exchange(formLoginRequest, String.class);
        val sessionAttributesFromDB = SessionTestUtils.getSessionAttributeBytesFromDb(conn);
        Assertions.assertThat(sessionAttributesFromDB.size()).isEqualTo(1);
        String firstAttributesKey = (String) sessionAttributesFromDB.keySet().toArray()[0];
        Assertions.assertThat(firstAttributesKey).endsWith("CSRF_TOKEN");
        // Verify CSRF token format and properties
        byte[] tokenBytes = sessionAttributesFromDB.get(firstAttributesKey);
        Assertions.assertThat(tokenBytes).isNotNull();
        Assertions.assertThat(tokenBytes.length).isGreaterThan(0);
    }
}
