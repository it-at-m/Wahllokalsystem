package de.muenchen.refarch.gateway.configuration;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import de.muenchen.refarch.gateway.OAuthSecurityMockConfiguration;
import de.muenchen.refarch.gateway.TestConstants;
import de.muenchen.refarch.gateway.security.SingleFlightReactiveOAuth2AuthorizedClientManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { TestConstants.SPRING_TEST_PROFILE })
@AutoConfigureObservability
@Import(OAuthSecurityMockConfiguration.class)
class SecurityConfigurationTest {
    @Autowired
    private WebTestClient api;

    @Autowired
    private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @Test
    void should_configureSingleFlightAuthorizedClientManager() {
        assertInstanceOf(SingleFlightReactiveOAuth2AuthorizedClientManager.class, authorizedClientManager);
    }

    @Test
    void accessSecuredResourceRootThenUnauthorized() {
        // 302 is returned instead of 401 because auf cookie session
        api.get().uri("/").exchange().expectStatus().isFound();
    }

    @Test
    void accessSecuredResourceClientsThenUnauthorized() {
        api.get().uri("/clients/test").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void accessUnsecuredResourceActuatorHealthThenOk() {
        api.get().uri("/actuator/health").exchange().expectStatus().isOk();
    }

    @Test
    void accessUnsecuredResourceActuatorHealthLivenessThenOk() {
        api.get().uri("/actuator/health/liveness").exchange().expectStatus().isOk();
    }

    @Test
    void accessUnsecuredResourceActuatorHealthReadinessThenOk() {
        api.get().uri("/actuator/health/readiness").exchange().expectStatus().isOk();
    }

    @Test
    void accessUnsecuredResourceActuatorInfoThenOk() {
        api.get().uri("/actuator/info").exchange().expectStatus().isOk();
    }

    @Test
    void accessUnsecuredResourceActuatorMetricsThenOk() {
        api.get().uri("/actuator/metrics").exchange().expectStatus().isOk();
    }
}
