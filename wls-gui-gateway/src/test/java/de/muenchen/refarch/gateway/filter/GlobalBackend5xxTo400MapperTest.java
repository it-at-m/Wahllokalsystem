package de.muenchen.refarch.gateway.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static de.muenchen.refarch.gateway.TestConstants.SPRING_TEST_PROFILE;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import de.muenchen.refarch.gateway.OAuthSecurityMockConfiguration;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(SPRING_TEST_PROFILE)
@AutoConfigureWireMock
@TestPropertySource(
        properties = {
                "config.map5xxto400=true",
        }
)
@Import(OAuthSecurityMockConfiguration.class)
class GlobalBackend5xxTo400MapperTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    void backendError500() {
        stubFor(get(urlEqualTo("/remote"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeaders(new HttpHeaders(
                                new HttpHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()),
                                new HttpHeader(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE,
                                        "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\"")))
                        .withBody("{ \"testkey\" : \"testvalue\" }")));

        webTestClient.get().uri("/api/refarch-gateway-backend-service/remote").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().valueMatches(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .expectHeader().doesNotExist(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE)
                .expectBody()
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("Bad Request");
    }

    @Test
    @WithMockUser
    void backendError200() {
        stubFor(get(urlEqualTo("/remote"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeaders(new HttpHeaders(
                                new HttpHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()),
                                new HttpHeader(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE,
                                        "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\"")))
                        .withBody("{ \"testkey\" : \"testvalue\" }")));

        webTestClient.get().uri("/api/refarch-gateway-backend-service/remote").exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueMatches(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .expectHeader().doesNotExist(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE)
                .expectBody()
                .jsonPath("$.testkey").isEqualTo("testvalue");
    }

}
