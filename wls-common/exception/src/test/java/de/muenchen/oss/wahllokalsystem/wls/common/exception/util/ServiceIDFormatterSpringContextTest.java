package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ServiceIDFormatter.class, properties = "service.info.oid=My app name")
class ServiceIDFormatterSpringContextTest {

    @Autowired
    ServiceIDFormatter serviceIDFormatter;

    @Nested
    class GetId {

        @Test
        void should_returnServiceId_when_propertyIsDefinedInSpringContext() {
            Assertions.assertThat(serviceIDFormatter.getId()).contains("My app name");
        }
    }
}
