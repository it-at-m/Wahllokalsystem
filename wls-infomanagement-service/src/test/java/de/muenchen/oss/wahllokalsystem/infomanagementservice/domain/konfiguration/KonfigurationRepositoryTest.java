package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
class KonfigurationRepositoryTest {

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_KONFIGURATION);
        konfigurationRepository.deleteAll();
    }

    @Test
    void getFruehesteLoginUhrzeit() {
        val konfigurationFruehesteLoginUhrzeit = new Konfiguration("FRUEHESTE_LOGIN_UHRZEIT", "06:00:00", "", "");
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_WRITE_KONFIGURATION);
        konfigurationRepository.save(konfigurationFruehesteLoginUhrzeit);
        SecurityContextHolder.clearContext();

        Assertions.assertThat(konfigurationRepository.getFruehesteLoginUhrzeit().get()).isEqualTo(konfigurationFruehesteLoginUhrzeit);
    }

    @Test
    void getSpaetesteLoginUhrzeit() {
        val konfigurationFruehesteLoginUhrzeit = new Konfiguration("SPAETESTE_LOGIN_UHRZEIT", "23:56:00", "", "");
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_WRITE_KONFIGURATION);
        konfigurationRepository.save(konfigurationFruehesteLoginUhrzeit);
        SecurityContextHolder.clearContext();

        Assertions.assertThat(konfigurationRepository.getSpaetesteLoginUhrzeit().get()).isEqualTo(konfigurationFruehesteLoginUhrzeit);
    }

    @Test
    void getWillkommenstext() {
        val konfigurationFruehesteLoginUhrzeit = new Konfiguration("WILLKOMMENSTEXT", "hello world", "", "");
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_WRITE_KONFIGURATION);
        konfigurationRepository.save(konfigurationFruehesteLoginUhrzeit);
        SecurityContextHolder.clearContext();

        Assertions.assertThat(konfigurationRepository.getWillkommenstext().get()).isEqualTo(konfigurationFruehesteLoginUhrzeit);
    }

}
