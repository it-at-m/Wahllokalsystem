package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.common.CryptoService;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.AESEncryptionConfiguration;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = { AESEncryptionConfiguration.class, CryptoService.class, ServiceIDFormatter.class },
        properties = { "service.config.crypto.key = 770A8A65DA156D24EE2A093277530142" }
)
class CryptoServiceIntegrationTest {

    @Autowired
    CryptoService cryptoService;

    @Test
    void should_useProvidedBeans_when_startingContext() {
        val valueToEncrypt = "Mzc2NTI2NzIzQUZEQUIzRA==";
        Assertions.assertThat(cryptoService.decrypt(cryptoService.encrypt(valueToEncrypt))).isEqualTo(valueToEncrypt);
    }
}
