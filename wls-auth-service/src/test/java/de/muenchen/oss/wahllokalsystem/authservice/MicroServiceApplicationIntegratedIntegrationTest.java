package de.muenchen.oss.wahllokalsystem.authservice;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_LDAP })
class MicroServiceApplicationIntegratedIntegrationTest {

    @Test
    void should_throwNoException_when_serviceIsStartedWithNoDummyRestClients() {

    }
}