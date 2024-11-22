package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.AuthorityRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class LdapConfigurationTest {

    private static final String WAHLTAG_ID = "WAHLTAG_ID";

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @LocalServerPort
    int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeEach
    void setup() {
        authorityRepository.deleteAll();
        userRepository.deleteUsersByWahltagID(WAHLTAG_ID);
    }

    @Nested
    class WithEmbeddedLdapServer {

        final Pattern csrfTokenPattern = Pattern.compile("<input .* id=\"csrf_token\".*\\r?\\n?.*value=\\\"(.*)\\\".*\\/>");

        @Test
        void should_loginAndAccessRessourceSuccessfully_when_usingEmbeddedUser() {
            val username = "user";

            transactionTemplate.executeWithoutResult(status -> {
                val authorityToSave = new Authority("WLS_WAHLVORSTAND", Collections.emptySet(), Collections.emptySet());
                val authoritySaved = authorityRepository.save(authorityToSave);
                val userToSave = new User(username, null, null, true, true, WAHLTAG_ID, null, null, null, null, null, new HashSet<>(Set.of(authoritySaved)),
                        null);
                userRepository.save(userToSave);
            });

            //try to access restricted resource
            val restrictedResourceRequestHeaders = new HttpHeaders();
            restrictedResourceRequestHeaders.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
            val restrictedResourceRequest = new RequestEntity<>(restrictedResourceRequestHeaders, HttpMethod.GET, URI.create(getHost() + "/demo?wahllokalgui"));
            val restrictedResourceResponse = restTemplate.exchange(restrictedResourceRequest, String.class);
            Assertions.assertThat(restrictedResourceResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

            val restrictedResourceRequestSessionID = getSessionID(restrictedResourceResponse);

            //Get Form and extract csrfToken
            val formLoginRequestHeaders = new HttpHeaders();
            formLoginRequestHeaders.add("Cookie", restrictedResourceRequestSessionID);
            val formLoginRequest = new RequestEntity<>(formLoginRequestHeaders, HttpMethod.GET, URI.create(getHost() + "/login"));
            val formLoginResponse = restTemplate.exchange(formLoginRequest, String.class);
            Assertions.assertThat(formLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            val csrfToken = getCsrfToken(formLoginResponse);

            //Login with user and get jSessionID
            val loginRequestHeaders = new HttpHeaders();
            loginRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
            loginRequestHeaders.add("Cookie", restrictedResourceRequestSessionID);
            val loginRequestBody = "username=" + username + "&password=password&_csrf=" + csrfToken;
            val loginRequest = new RequestEntity<>(loginRequestBody, loginRequestHeaders, HttpMethod.POST, URI.create(getHost() + "/login"));
            val loginResponse = restTemplate.exchange(loginRequest, String.class);
            Assertions.assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

            //verify resource access
            val resourceAccessHeaders = new HttpHeaders();
            resourceAccessHeaders.add("Cookie", restrictedResourceRequestSessionID);
            resourceAccessHeaders.add(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
            val resourceAccessRequest = new RequestEntity<>(resourceAccessHeaders, HttpMethod.GET, URI.create(getHost() + "/demo"));
            val resourceAccessResponse = restTemplate.exchange(resourceAccessRequest, String.class);
            Assertions.assertThat(resourceAccessResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
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

}
