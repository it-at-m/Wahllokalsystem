package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class ErgebnismeldungServiceSecurityTest {

    @Autowired
    private ErgebnismeldungService ergebnismeldungService;

    @Nested
    class SaveErgebnismeldung {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_SAVE_ERGEBNISMELDUNG);

            val ergebnismeldungToSave = ErgebnismeldungDTO.builder().wahlbezirkID("00000000-0000-0000-0000-000000000001").wahlID("wahlID1").meldungsart(null).aWerte(null).bWerte(null).wahlbriefeWerte(null).ungueltigeStimmzettels(null).ungueltigeStimmzettelAnzahl(null).ergebnisse(null).wahlart(null).build();

            Assertions.assertThatNoException().isThrownBy(() -> ergebnismeldungService.saveErgebnismeldung(ergebnismeldungToSave));
        }

        @Test
        void anyMissingAuthorityCausesFail() {
            SecurityUtils.runWith();

            val ergebnismeldungToSave = ErgebnismeldungDTO.builder().wahlbezirkID("00000000-0000-0000-0000-000000000001").wahlID("wahlID1").meldungsart(null).aWerte(null).bWerte(null).wahlbriefeWerte(null).ungueltigeStimmzettels(null).ungueltigeStimmzettelAnzahl(null).ergebnisse(null).wahlart(null).build();

            Assertions.assertThatException().isThrownBy(() -> ergebnismeldungService.saveErgebnismeldung(ergebnismeldungToSave))
                    .isInstanceOf(AccessDeniedException.class);
        }

    }
}
