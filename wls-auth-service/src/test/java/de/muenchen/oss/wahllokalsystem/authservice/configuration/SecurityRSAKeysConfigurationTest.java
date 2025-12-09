package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import com.nimbusds.jose.jwk.source.JWKSource;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;

class SecurityRSAKeysConfigurationTest {

  @AfterEach
  void teardown() {
    System.clearProperty("spring.profiles.active");
    System.clearProperty("service.config.oauth2.jwk.rsa.init.seed");
    System.clearProperty("service.config.rsa.rsaKeySetting");
    System.clearProperty("service.config.rsa.private-key");
    System.clearProperty("service.config.rsa.public-key");
  }

  @Nested
  class RSAKeyConfigured {

    @Test
    void should_failStartup_when_privateKeyIsMissing() {
      System.setProperty("spring.profiles.active", "test,dummy.ldap");
      System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
      System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
      System.setProperty("service.config.rsa.private-key", "");
      Throwable exception =
          Assertions.catchThrowableOfType(
              UnsatisfiedDependencyException.class,
              () -> SpringApplication.run(MicroServiceApplication.class));
      assertCauseIsNoInstantiatedJWKSource(exception);
    }

    @Test
    void should_failStartup_when_publicKeyIsMissing() {
      System.setProperty("spring.profiles.active", "test,dummy.ldap");
      System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
      System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
      System.setProperty("service.config.rsa.public-key", "");
      Throwable exception =
          Assertions.catchThrowableOfType(
              UnsatisfiedDependencyException.class,
              () -> SpringApplication.run(MicroServiceApplication.class));
      assertCauseIsNoInstantiatedJWKSource(exception);
    }

    @Test
    void should_failStartup_when_publicAndPrivateKeyAreMissing() {
      System.setProperty("spring.profiles.active", "test,dummy.ldap");
      System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
      System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
      System.setProperty("service.config.rsa.public-key", "");
      System.setProperty("service.config.rsa.private-key", "");
      Throwable exception =
          Assertions.catchThrowableOfType(
              UnsatisfiedDependencyException.class,
              () -> SpringApplication.run(MicroServiceApplication.class));
      assertCauseIsNoInstantiatedJWKSource(exception);
    }
  }

  @Nested
  class RSAKeyNotConfigured {
    @Test
    void should_failStartup_when_defaultRSASettingsAreUsed() {
      System.setProperty("spring.profiles.active", "dummy.ldap,testWithoutRSAKeys");
      System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
      Throwable exception =
          Assertions.catchThrowableOfType(
              UnsatisfiedDependencyException.class,
              () -> SpringApplication.run(MicroServiceApplication.class));
      assertCauseIsNoInstantiatedJWKSource(exception);
    }
  }

  private void assertCauseIsNoInstantiatedJWKSource(Throwable exception) {
    BeanInstantiationException causeWithJWKSource = null;
    do {
      if (exception.getCause() instanceof BeanInstantiationException bcex) {
        if (bcex.getBeanClass() == JWKSource.class) {
          causeWithJWKSource = bcex;
        }
      }

      if (exception != exception.getCause()) {
        exception = exception.getCause();
      } else {
        exception = null;
      }
    } while (exception != null && causeWithJWKSource == null);
    Assertions.assertThat(causeWithJWKSource).isNotNull();
  }
}
