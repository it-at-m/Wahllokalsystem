package de.muenchen.oss.wahllokalsystem.infomanagementservice;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AnonymousDetailRetriever;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_NO_SECURITY_PROFILE)
class MicroServiceApplicationNoSecurityProfileTest {

    @Autowired
    AnonymousDetailRetriever anonymousDetailRetriever;

    @Test
    void should_returnNotNull_when_anonymouseHandlerIsUsedFromAutowiredContext() {
        Assertions.assertThat(anonymousDetailRetriever).isNotNull();
    }
}
