package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ServiceIDFormatterTest {

    @Test
    void getId() {
        val appName = "app name";
        val unitUnderTest = new ServiceIDFormatter(appName);

        Assertions.assertThat(unitUnderTest.getId()).isEqualTo(appName);
    }
}
