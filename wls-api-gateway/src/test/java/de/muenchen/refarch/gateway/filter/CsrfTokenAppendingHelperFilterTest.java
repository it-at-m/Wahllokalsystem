package de.muenchen.refarch.gateway.filter;

import static de.muenchen.refarch.gateway.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.refarch.gateway.OAuthSecurityMockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(SPRING_TEST_PROFILE)
@Import(OAuthSecurityMockConfiguration.class)
class CsrfTokenAppendingHelperFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser
    void csrfCookieAppendition() {
        webTestClient.get().uri("/").exchange()
                .expectHeader()
                .valueMatches("set-cookie", "XSRF-TOKEN=[a-f\\d]{8}(-[a-f\\d]{4}){3}-[a-f\\d]{12}?;\\sPath=/");
    }

}
