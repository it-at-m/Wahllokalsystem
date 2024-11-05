package de.muenchen.oss.wahllokalsystem.wls.common.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AESEncryptionConfiguration {

    private static final String AES = "AES";

    @Value("${app.crypto.key}")
    String key;

    @Bean
    Cipher encryptionCipher() throws Exception {
        val secret = new SecretKeySpec(key.getBytes(), 0, 16, AES);
        val encryptCipher = Cipher.getInstance(AES);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        return encryptCipher;
    }

    @Bean
    Cipher decryptionCipher() throws Exception {
        val secret = new SecretKeySpec(key.getBytes(), 0, 16, AES);
        val _encryptCipher = Cipher.getInstance(AES);
        _encryptCipher.init(Cipher.DECRYPT_MODE, secret);
        return _encryptCipher;
    }
}
