package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import de.muenchen.oss.wahllokalsystem.wls.common.security.EncryptionBuilder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfiguration {

    @Bean
    public EncryptionBuilder encryptionBuilder(@Value("{serviceauth.crypto.key}") final String cryptoKey, final ServiceIDFormatter serviceIDFormatter)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return new EncryptionBuilder(cryptoKey.getBytes(), serviceIDFormatter);
    }
}
