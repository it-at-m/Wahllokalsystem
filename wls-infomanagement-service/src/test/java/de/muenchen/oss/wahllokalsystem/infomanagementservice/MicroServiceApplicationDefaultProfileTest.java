package de.muenchen.oss.wahllokalsystem.infomanagementservice;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AnonymousHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class)
class MicroServiceApplicationDefaultProfileTest {

    @Autowired(required = false)
    AnonymousHandler anonymousHandler;

    @Test
    void anonymouseHandlerIsNotPartOfContext() {
        Assertions.assertThat(anonymousHandler).isNull();
    }

}
