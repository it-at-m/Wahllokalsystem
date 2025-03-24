package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

import com.nimbusds.jose.jwk.source.JWKSource;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = AFTER_CLASS)
class SecurityRSAKeysConfigurationTest {

    @Nested
    class RSAKeyConfigured {

        @Test
        void should_failStartup_when_privateKeyIsMissing() {
            System.setProperty("spring.profiles.active", "test,dummy.ldap");
            System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
            System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
            //  System.setProperty("service.config.rsa.private-key", "");
            Throwable exception = Assertions.catchThrowableOfType(
                    UnsatisfiedDependencyException.class, () -> SpringApplication.run(MicroServiceApplication.class));
            assertForCorrectException(exception);
        }

        @Test
        void should_failStartup_when_publicKeyIsMissing() {
            System.setProperty("spring.profiles.active", "test,dummy.ldap");
            System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
            System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
            // System.setProperty("service.config.rsa.public-key", "");
            Throwable exception = Assertions.catchThrowableOfType(
                    UnsatisfiedDependencyException.class, () -> SpringApplication.run(MicroServiceApplication.class));
            assertForCorrectException(exception);
        }

        @Test
        void should_failStartup_when_publicAndPrivateKeyAreMissing() {
            System.setProperty("spring.profiles.active", "test,dummy.ldap");
            System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
            System.setProperty("service.config.rsa.rsaKeySetting", "STATIC_KEY");
            //            System.setProperty("service.config.rsa.public-key", "");
            //            System.setProperty("service.config.rsa.private-key", "");
            Throwable exception = Assertions.catchThrowableOfType(
                    UnsatisfiedDependencyException.class, () -> SpringApplication.run(MicroServiceApplication.class));
            assertForCorrectException(exception);
        }

        private void assertForCorrectException(Throwable exception) {
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

    @Nested
    class RSAKeyNotConfigured {
        @Test
        void should_failStartup_when_defaultRSASettingsAreUsed() {
            System.setProperty("spring.profiles.active", "dummy.ldap,testWithoutRSAKeys");
            System.setProperty("service.config.oauth2.jwk.rsa.init.seed", "seed");
            Throwable exception = Assertions.catchThrowableOfType(
                    UnsatisfiedDependencyException.class, () -> SpringApplication.run(MicroServiceApplication.class));
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
}
