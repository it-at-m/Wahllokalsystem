package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AESEncryptionConfiguration.class, EncryptionBuilder.class, ServiceIDFormatter.class}, properties = {"app.crypto.key = 770A8A65DA156D24EE2A093277530142"})
class EncryptionBuilderIntegrationTest {

    @Autowired
    EncryptionBuilder encryptionBuilder;

    @Test
    void should_useProvidedBeans_when_startingContext() {
        Assertions.assertThatNoException().isThrownBy(() -> encryptionBuilder.encryptValue("Mzc2NTI2NzIzQUZEQUIzRA=="));
    }
}