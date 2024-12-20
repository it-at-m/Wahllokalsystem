package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ServiceIDFormatterTest {

    @Nested
    class GetId {

        @Test
        void should_returnDefinedId_when_called() {
            val appName = "app name";
            val unitUnderTest = new ServiceIDFormatter(appName);

            Assertions.assertThat(unitUnderTest.getId()).isEqualTo(appName);
        }
    }
}
