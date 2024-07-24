package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WahlbeteiligungSecurityTest {

    @Autowired
    WahlbeteiligungService unitUnderTest;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class SaveWahlvorbereitung {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_SAVE_WAHLBETEILIGUNG);

            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val modelToSet = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.saveWahlbeteiligung(modelToSet));
        }

        @Test
        void accessDeniedOnServiceWithoutAuthority() {
            SecurityUtils.runWith();

            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val modelToSet = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            Assertions.assertThatThrownBy(() -> unitUnderTest.saveWahlbeteiligung(modelToSet))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
