package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import java.net.URI;
import java.util.regex.Pattern;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class LdapConfigurationTest {

    @LocalServerPort
    int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Nested
    class WithEmbeddedLdapServer {

        final Pattern csrfTokenPattern = Pattern.compile("<input .* id=\"csrf_token\".*\\r?\\n?.*value=\\\"(.*)\\\".*\\/>");

        @Test
        void should_loginAndAccessRessourceSuccessfully_when_usingEmbeddedUser() {
            //Get Form and extract csrfToken
            val formLoginRequest = new RequestEntity<>(HttpMethod.GET, URI.create(getHost() + "/login"));
            val formLoginResponse = restTemplate.exchange(formLoginRequest, String.class);
            Assertions.assertThat(formLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            val csrfToken = getCsrfToken(formLoginResponse);

            val formLoginSessionID = getSessionID(formLoginResponse);

            //Login with user and get jSessionID
            val loginRequestHeaders = new HttpHeaders();
            loginRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
            loginRequestHeaders.add("Cookie", formLoginSessionID);
            val loginRequestBody = "username=user&password=password&_csrf=" + csrfToken;
            val loginRequest = new RequestEntity<>(loginRequestBody, loginRequestHeaders, HttpMethod.POST, URI.create(getHost() + "/login"));
            val loginResponse = restTemplate.exchange(loginRequest, String.class);
            Assertions.assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

            val loginRequestSessionID = getSessionID(loginResponse);

            //verify resource access
            val resourceAccessHeaders = new HttpHeaders();
            resourceAccessHeaders.add("Cookie", loginRequestSessionID);
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
