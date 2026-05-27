package de.muenchen.refarch.gateway.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static de.muenchen.refarch.gateway.TestConstants.SPRING_TEST_PROFILE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.refarch.gateway.OAuthSecurityMockConfiguration;
import lombok.val;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Nested;
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
                "service.generic500Exception.code=1234567890",
                "service.generic500Exception.message=genericDefaultMessage",
                "service.info.oid=serviceID"
        }
)
@Import(OAuthSecurityMockConfiguration.class)
class GlobalBackendErrorFilterTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Filter {
        @Test
        @WithMockUser
        void should_returnExistingWlsException_when_backendReturns5xxWithWlsException() throws Exception {
            val wlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "1234", "serviceID", "message");
            val wlsExceptionDTOAsString = objectMapper.writeValueAsString(wlsExceptionDTO);
            stubFor(get(urlEqualTo("/remote"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .withHeaders(new HttpHeaders(
                                    new HttpHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()),
                                    new HttpHeader(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE,
                                            "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\"")))
                            .withBody(wlsExceptionDTOAsString)));

            webTestClient.get().uri("/api/refarch-gateway-backend-service/remote").exchange()
                    .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                    .expectHeader().valueMatches(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                    .expectHeader().doesNotExist(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE)
                    .expectBody()
                    .jsonPath("$.code").isEqualTo(wlsExceptionDTO.code())
                    .jsonPath("$.category").isEqualTo(wlsExceptionDTO.category())
                    .jsonPath("$.message").isEqualTo(wlsExceptionDTO.message())
                    .jsonPath("$.service").isEqualTo(wlsExceptionDTO.service());
        }

        @Test
        @WithMockUser
        void should_returnDefaultWlsException_when_backendReturns5xxWithoutWlsException() throws Exception {
            stubFor(get(urlEqualTo("/remote"))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .withHeaders(new HttpHeaders(
                                    new HttpHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()),
                                    new HttpHeader(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE,
                                            "Bearer realm=\"Access to the staging site\", charset=\"UTF-8\"")))
                            .withBody("some strange string")));

            webTestClient.get().uri("/api/refarch-gateway-backend-service/remote").exchange()
                    .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                    .expectHeader().valueMatches(org.springframework.http.HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                    .expectHeader().doesNotExist(org.springframework.http.HttpHeaders.WWW_AUTHENTICATE)
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("1234567890")
                    .jsonPath("$.category").isEqualTo(WlsExceptionCategory.T)
                    .jsonPath("$.message").isEqualTo("genericDefaultMessage")
                    .jsonPath("$.service").isEqualTo("serviceID");
        }
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
