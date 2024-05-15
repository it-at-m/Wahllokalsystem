package de.muenchen.oss.wahllokalsystem.infomanagementservice;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AnonymousHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_NO_SECURITY_PROFILE)
public class MicroServiceApplication_NoSecurityProfile_Test {

    @Autowired
    AnonymousHandler anonymousHandler;

    @Test
    void anonymousHandlerIsPartOfContext() {
        Assertions.assertThat(anonymousHandler).isNotNull();
    }
}
