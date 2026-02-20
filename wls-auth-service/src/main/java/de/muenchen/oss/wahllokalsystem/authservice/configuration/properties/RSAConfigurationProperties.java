package de.muenchen.oss.wahllokalsystem.authservice.configuration.properties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "service.config.rsa", ignoreInvalidFields = true)
public class RSAConfigurationProperties {
  private RSAPrivateKey privateKey;
  private RSAPublicKey publicKey;
  private RSAKeySetting rsaKeySetting = RSAKeySetting.STATIC_KEY;
}
