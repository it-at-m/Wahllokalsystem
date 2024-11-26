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
import java.util.regex.Pattern;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.context.ActiveProfiles;

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
class SessionCreationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate testRestTemplate = new TestRestTemplate();

    private final Connection conn = DriverManager.getConnection("jdbc:h2:mem:wls-auth-service", "sa", "");

    @Autowired
    SessionRegistry sessionRegistry;

    final Pattern csrfTokenPattern = Pattern.compile("<input .* id=\"csrf_token\".*\\r?\\n?.*value=\\\"(.*)\\\".*\\/>");

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
                0, SessionUtils.getSessionIdsFromDatabase(conn).size());
        assertEquals(
                0, SessionUtils.getSessionAttributeBytesFromDb(conn).size());
    }

    @Test
    public void should_createSessionInDatabaseAndInSessionRegistry_when_logingInn() throws SQLException {
        //Get Form and extract csrfToken
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/login"));
        val formLoginResponse = testRestTemplate.exchange(formLoginRequest, String.class);
        Assertions.assertThat(formLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        val csrfToken = getCsrfToken(formLoginResponse);
        val formLoginSessionID = getSessionID(formLoginResponse);
        //Login with user and get jSessionID
        val loginRequestHeaders = new HttpHeaders();
        loginRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        loginRequestHeaders.add("Cookie", formLoginSessionID);
        val loginRequestBody = "username=user&password=password&_csrf=" + csrfToken;
        val loginRequest = new RequestEntity<>(loginRequestBody, loginRequestHeaders, HttpMethod.POST, URI.create(getHost() + "/login"));
        val loginResponse = testRestTemplate.exchange(loginRequest, String.class);
        Assertions.assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        val sessionIdsFromDB = SessionUtils.getSessionIdsFromDatabase(conn);
        val allPrincipalsInRegistry = sessionRegistry.getAllPrincipals();

        assertEquals(1, sessionIdsFromDB.size());
        assertEquals(1, allPrincipalsInRegistry.size());
        val sessionIDFromDB = sessionIdsFromDB.get(0);
        val sessionIDFromRegistry = sessionRegistry.getAllSessions(allPrincipalsInRegistry.get(0), true).get(0).getSessionId();
        assertEquals(sessionIDFromDB, sessionIDFromRegistry);
    }

    @Test
    public void should_createCsrfSessionAttribute_when_callingLogin() throws SQLException {
        val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:" + port + "/login"));
        this.testRestTemplate.exchange(formLoginRequest, String.class);
        val sessionAttributesFromDB = SessionUtils.getSessionAttributeBytesFromDb(conn);
        assertEquals(1, sessionAttributesFromDB.size());
        String firstAttributesKey = (String) sessionAttributesFromDB.keySet().toArray()[0];
        Assertions.assertThat(firstAttributesKey).endsWith("CSRF_TOKEN");
    }

    private String getHost() {
        return "http://localhost:" + port;
    }

    private String getSessionID(final ResponseEntity<?> response) {
        val setCookieHeader = response.getHeaders().get("Set-Cookie");
        if (setCookieHeader == null) {
            return null;
        }
        val cookieHeader = setCookieHeader.get(0);
        if (cookieHeader == null) {
            return null;
        }
        return cookieHeader.substring(0, cookieHeader.indexOf(";"));
    }

    private String getCsrfToken(final ResponseEntity<String> response) {
        val csrfTokenMatcher = csrfTokenPattern.matcher(response.getBody());
        Assertions.assertThat(csrfTokenMatcher.find()).isTrue();
        return csrfTokenMatcher.group(1);
    }

}
