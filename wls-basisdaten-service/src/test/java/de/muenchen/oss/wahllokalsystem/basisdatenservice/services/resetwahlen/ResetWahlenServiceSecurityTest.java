package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = MicroServiceApplication.class)
public class ResetWahlenServiceSecurityTest {

    @Autowired
    ResetWahlenService resetWahlenService;

    @Autowired
    WahlRepository wahlRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHL);
        wahlRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Nested
    class ResetWahlen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
            Assertions.assertThatNoException().isThrownBy(() -> resetWahlenService.resetWahlen());
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL, Authorities.REPOSITORY_WRITE_WAHL, Authorities.REPOSITORY_DELETE_WAHL);
            Assertions.assertThatThrownBy(() -> resetWahlenService.resetWahlen()).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void technischeWlsExceptionWhenRepoAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_RESET_WAHLEN);
            Assertions.assertThatThrownBy(() -> resetWahlenService.resetWahlen()).isInstanceOf(TechnischeWlsException.class);
        }
    }
}
