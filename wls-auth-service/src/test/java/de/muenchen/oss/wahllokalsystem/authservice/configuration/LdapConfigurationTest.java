package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.AuthorityRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Permission;
import de.muenchen.oss.wahllokalsystem.authservice.domain.PermissionRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import de.muenchen.oss.wahllokalsystem.authservice.rest.UserDTO;
import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
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
    PermissionRepository permissionRepository;

    @Autowired
    RegisteredClientRepository registeredClientRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @LocalServerPort
    int port;

    @Value("${service.config.oauth2.clients.wahllokalgui.id}")
    String wahllokalGui;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeEach
    void setup() {
        authorityRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.executeWithoutResult(status -> {
            authorityRepository.deleteAll();
            permissionRepository.deleteAll();
            userRepository.deleteUsersByWahltagID(WAHLTAG_ID);
        });
    }

    @Nested
    class WithEmbeddedLdapServer {

        final String EMBEDDED_LDAP_USER = "wls_all";
        final String EMBEDDED_LDAP_PASSWORD = "test";

        final Pattern csrfTokenPattern = Pattern.compile("<input .* id=\"csrf_token\".*\\r?\\n?.*value=\\\"(.*)\\\".*\\/>");
        final Pattern codePattern = Pattern.compile("code=(.*)");

        @Test
        void should_accessSecureResource_when_loggingInWithToken() throws Exception {
            transactionTemplate.executeWithoutResult(status -> {
                val savedPermission = permissionRepository.save(new Permission("permission"));
                val authorityToSave = new Authority("WLS_WAHLVORSTAND", new HashSet<>(Set.of(savedPermission)), Collections.emptySet());
                val authoritySaved = authorityRepository.save(authorityToSave);
                val userToSave = new User(EMBEDDED_LDAP_USER, null, null, true, true, WAHLTAG_ID, null, null, null, null, null,
                        new HashSet<>(Set.of(authoritySaved)),
                        null);
                userRepository.save(userToSave);
            });

            val clientID = "wls";
            val clientSecret = "top-secret";
            transactionTemplate.executeWithoutResult(status -> {
                RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientID)
                        .clientSecret("{noop}" + clientSecret)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .redirectUri("http://127.0.0.1:8080/oauth2/authorized")
                        .postLogoutRedirectUri("http://localhost:8080/")
                        .scope(OidcScopes.OPENID)
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .build();

                registeredClientRepository.save(oidcClient);
            });

            val redirectURI = "http%3A%2F%2F127.0.0.1%3A" + port + "%2Foauth2%2Fauthorized";

            //init request authorization
            val restrictedResourceRequestHeaders = new HttpHeaders();
            restrictedResourceRequestHeaders.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
            val restrictedResourceRequest = new RequestEntity<>(restrictedResourceRequestHeaders, HttpMethod.GET, URI.create(
                    getHost() + "/oauth2/authorize?" + wahllokalGui + "&response_type=code&client_id=" + clientID + "&scope=openid&redirect_uri="
                            + redirectURI));
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
            loginRequestHeaders.add("referer", getHost() + "/login");
            val loginRequestBody = "username=" + EMBEDDED_LDAP_USER + "&password=" + EMBEDDED_LDAP_PASSWORD + "&_csrf=" + csrfToken;
            val loginRequest = new RequestEntity<>(loginRequestBody, loginRequestHeaders, HttpMethod.POST, URI.create(getHost() + "/login"));
            val loginResponse = restTemplate.exchange(loginRequest, String.class);

            val authorizeRelocation = loginResponse.getHeaders().getFirst("Location");

            //Authorize-Request
            val authorizeRequestHeaders = new HttpHeaders();
            authorizeRequestHeaders.add("Cookie", restrictedResourceRequestSessionID);
            val authorizeRequest = new RequestEntity<>(authorizeRequestHeaders, HttpMethod.GET, URI.create(authorizeRelocation));
            val authorizeResponse = restTemplate.exchange(authorizeRequest, String.class);
            Assertions.assertThat(authorizeResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

            val code = getCode(authorizeResponse.getHeaders().getFirst("Location"));

            //request token
            val tokenRequestHeaders = new HttpHeaders();
            tokenRequestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
            tokenRequestHeaders.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes()));
            tokenRequestHeaders.add("Cookie", restrictedResourceRequestSessionID);
            val tokenRequestBody = "grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectURI;
            val tokenRequest = new RequestEntity<>(tokenRequestBody, tokenRequestHeaders, HttpMethod.POST, URI.create(getHost() + "/oauth2/token"));
            val tokenResponse = restTemplate.exchange(tokenRequest, String.class);

            val token = objectMapper.readValue(tokenResponse.getBody(), Token.class).getAccess_token();

            //user endpoint
            val userRequestHeaders = new HttpHeaders();
            userRequestHeaders.add("Authorization", "Bearer " + token);
            val userRequest = new RequestEntity<>(userRequestHeaders, HttpMethod.GET, URI.create(getHost() + "/user"));
            val userResponse = restTemplate.exchange(userRequest, UserDTO.class);
            Assertions.assertThat(userResponse.getBody().authorities()).isNotEmpty();
        }

        private String getHost() {
            return "http://localhost:" + port;
        }

        private String getCode(String locationWithCode) {
            val codeMatcher = codePattern.matcher(locationWithCode);
            Assertions.assertThat(codeMatcher.find()).isTrue();
            return codeMatcher.group(1);
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

    @Data
    static class Token {
        private String access_token;
    }

}
