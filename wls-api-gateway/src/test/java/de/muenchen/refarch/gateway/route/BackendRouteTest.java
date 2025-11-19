package de.muenchen.refarch.gateway.route;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.muenchen.refarch.gateway.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import de.muenchen.refarch.gateway.OAuthSecurityMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(SPRING_TEST_PROFILE)
@AutoConfigureWireMock
@Import(OAuthSecurityMockConfiguration.class)
class BackendRouteTest {
    public static final String XSRF_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String TEST_KEY = "testkey";
    public static final String TEST_VALUE = "testvalue";
    private static final String TEST_JSON = "{ \"" + TEST_KEY + "\" : \"" + TEST_VALUE + "\" }";
    public static final String TEST_KEY_EXPRESSION = "$." + TEST_KEY;

    @Autowired
    private ApplicationContext context;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        // setup web test client
        webTestClient = WebTestClient.bindToApplicationContext(context)
                .apply(springSecurity())
                .configureClient()
                .build();
        // setup wiremock routes
        stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeaders(new HttpHeaders(
                                new HttpHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json"),
                                new HttpHeader(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE,
                                        "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\"") // removed by route filter
                        ))
                        .withBody(TEST_JSON)));
        stubFor(post(urlEqualTo("/test"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(TEST_JSON)));
    }

    @Test
    @WithMockUser
    void backendGetSuccess() {
        webTestClient
                .get().uri("/api/refarch-gateway-backend-service/test")
                .header(org.springframework.http.HttpHeaders.COOKIE,
                        "SESSION=5cfb01a3-b691-4ca9-8735-a05690e6c2ec; XSRF-TOKEN=4d82f9f1-41f6-4a09-994a-df99d30d1be9") // removed by default-filter
                .header(XSRF_HEADER_NAME, "5cfb01a3-b691-4ca9-8735-a05690e6c2ec") // angular specific -> removed by default-filter
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/hal+json")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueMatches(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json")
                .expectHeader().doesNotExist(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE)
                .expectBody().jsonPath(TEST_KEY_EXPRESSION).isEqualTo(TEST_VALUE);

        verify(getRequestedFor(urlEqualTo("/test"))
                .withoutHeader(org.springframework.http.HttpHeaders.COOKIE)
                .withoutHeader(XSRF_HEADER_NAME)
                .withHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, new EqualToPattern("application/hal+json")));
    }

    @Test
    void backendGetForbidden() {
        webTestClient
                .get().uri("/api/refarch-gateway-backend-service/test")
                .exchange()
                // because redirect to login
                .expectStatus().isEqualTo(HttpStatus.FOUND)
                .expectHeader().valueMatches(org.springframework.http.HttpHeaders.LOCATION, ".*/login.*");
    }

    @Test
    void publicGetSuccess() {
        webTestClient
                .get().uri("/public/api/refarch-gateway-backend-service/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(TEST_KEY_EXPRESSION).isEqualTo(TEST_VALUE);
    }

    @Test
    void publicPostSuccess() {
        webTestClient
                .mutateWith(csrf())
                .post().uri("/public/api/refarch-gateway-backend-service/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(TEST_KEY_EXPRESSION).isEqualTo(TEST_VALUE);
    }

    @Test
    void clientGetSuccess() {
        webTestClient
                .mutateWith(mockJwt())
                .get().uri("/clients/api/refarch-gateway-backend-service/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath(TEST_KEY_EXPRESSION).isEqualTo(TEST_VALUE);
    }

    @Test
    void clientGetForbidden() {
        webTestClient
                .get().uri("/clients/api/refarch-gateway-backend-service/test")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
