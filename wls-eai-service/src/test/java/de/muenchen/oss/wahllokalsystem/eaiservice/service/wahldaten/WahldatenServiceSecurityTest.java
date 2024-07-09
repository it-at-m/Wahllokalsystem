package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WahldatenServiceSecurityTest {

    @Autowired
    WahldatenService wahldatenService;

    @Nested
    class GetWahltage {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLTAGE);

            Assertions.assertThatNoException().isThrownBy(() -> wahldatenService.getWahltage(LocalDate.now()));
        }

        @Test
        void authorityIsMissing() {
            SecurityUtils.runWith();

            Assertions.assertThatException().isThrownBy(() -> wahldatenService.getWahltage(LocalDate.now())).isInstanceOf(AccessDeniedException.class);
        }

    }

    @Nested
    class GetWahlen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLEN);

            Assertions.assertThatNoException().isThrownBy(() -> wahldatenService.getWahlen(LocalDate.now(), "nummer"));
        }

        @Test
        void authorityIsMissing() {
            SecurityUtils.runWith();

            Assertions.assertThatException().isThrownBy(() -> wahldatenService.getWahlen(LocalDate.now(), "nummer")).isInstanceOf(AccessDeniedException.class);
        }

    }

    @Nested
    class GetWahlbezirke {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLBEZIRKE);

            Assertions.assertThatNoException().isThrownBy(() -> wahldatenService.getWahlbezirke(LocalDate.now(), "nummer"));
        }

        @Test
        void authorityIsMissing() {
            SecurityUtils.runWith();

            Assertions.assertThatException().isThrownBy(() -> wahldatenService.getWahlbezirke(LocalDate.now(), "nummer"))
                    .isInstanceOf(AccessDeniedException.class);
        }

    }

    @Nested
    class GetWahlberechtigte {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLBERECHTIGTE);

            Assertions.assertThatNoException().isThrownBy(() -> wahldatenService.getWahlberechtigte(UUID.randomUUID().toString()));
        }

        @Test
        void authorityIsMissing() {
            SecurityUtils.runWith();

            Assertions.assertThatException().isThrownBy(() -> wahldatenService.getWahlberechtigte(UUID.randomUUID().toString()))
                    .isInstanceOf(AccessDeniedException.class);
        }

    }

    @Nested
    class GetBasisdaten {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_LOAD_BASISDATEN);

            Assertions.assertThatNoException().isThrownBy(() -> wahldatenService.getBasisdaten(LocalDate.now(), "nummer"));
        }

        @Test
        void authorityIsMissing() {
            SecurityUtils.runWith();

            Assertions.assertThatException().isThrownBy(() -> wahldatenService.getBasisdaten(LocalDate.now(), "nummer"))
                    .isInstanceOf(AccessDeniedException.class);
        }

    }
}
