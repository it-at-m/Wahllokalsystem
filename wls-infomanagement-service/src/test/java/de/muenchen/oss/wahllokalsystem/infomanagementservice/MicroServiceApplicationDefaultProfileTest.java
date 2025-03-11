package de.muenchen.oss.wahllokalsystem.infomanagementservice;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AnonymousDetailRetriever;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class)
class MicroServiceApplicationDefaultProfileTest {

    @Autowired(required = false)
    AnonymousDetailRetriever anonymousDetailRetriever;

    @Test
    void should_returnNull_when_anonymouseHandlerIsUsedFromAutowiredContext() {
        Assertions.assertThat(anonymousDetailRetriever).isNull();
    }

}
