package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeListeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag.WahlvorschlagService;
import java.time.LocalDate;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlvorschlagControllerTest {

    @Mock
    WahlvorschlagService wahlvorschlagService;

    @InjectMocks
    WahlvorschlagController unitUnderTest;

    @Nested
    class LoadWahlvorschlaege {

        @Test
        void gotDataFromService() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "stimmzettelgebietID";

            val wahlvorschlaegeFromService = new WahlvorschlaegeDTO(wahlbezirkID, wahlID, stimmzettelgebietID, Collections.emptySet());

            Mockito.when(wahlvorschlagService.getWahlvorschlaegeForWahlAndWahlbezirk(wahlID, wahlbezirkID)).thenReturn(wahlvorschlaegeFromService);

            Assertions.assertThat(unitUnderTest.loadWahlvorschlaege(wahlID, wahlbezirkID)).isSameAs(wahlvorschlaegeFromService);
        }

    }

    @Nested
    class LoadReferendumvorlagen {

        @Test
        void gotDataFromService() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "stimmzettelgebietID";

            val referendumVorlagenFromService = new ReferendumvorlagenDTO(wahlbezirkID, Collections.emptySet());

            Mockito.when(wahlvorschlagService.getReferendumvorlagenForWahlAndWahlbezirk(wahlID, wahlbezirkID)).thenReturn(referendumVorlagenFromService);

            Assertions.assertThat(unitUnderTest.loadReferendumvorlagen(wahlID, wahlbezirkID)).isSameAs(referendumVorlagenFromService);
        }
    }

    @Nested
    class LoadWahlvorschlaegeListe {

        @Test
        void gotDataFromService() {
            val wahltag = LocalDate.of(2024, 10, 10);
            val wahlID = "wahlID";

            val wahlvorschlaegeListeFromService = new WahlvorschlaegeListeDTO(wahlID, Collections.emptySet());

            Mockito.when(wahlvorschlagService.getWahlvorschlaegeListeForWahltagAndWahlID(wahltag, wahlID)).thenReturn(wahlvorschlaegeListeFromService);

            Assertions.assertThat(unitUnderTest.loadWahlvorschlaegeListe(wahltag, wahlID)).isSameAs(wahlvorschlaegeListeFromService);
        }
    }

}
