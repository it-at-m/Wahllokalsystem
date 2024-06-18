package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
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
class WahlvorstandControllerTest {

    @Mock
    WahlvorstandService wahlvorstandService;

    @InjectMocks
    WahlvorstandController unitUnderTest;

    @Nested
    class LoadWahlvorstand {

        @Test
        void gotDataFromService() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlvorstandFromService = new WahlvorstandDTO("wahlbezirkID", Collections.emptySet());

            Mockito.when(wahlvorstandService.getWahlvorstandForWahlbezirk(wahlbezirkID)).thenReturn(Optional.of(wahlvorstandFromService));

            Assertions.assertThat(unitUnderTest.loadWahlvorstand(wahlbezirkID)).isSameAs(wahlvorstandFromService);
        }

        @Test
        void gotNoDataFromService() {

            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(wahlvorstandService.getWahlvorstandForWahlbezirk(wahlbezirkID)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.loadWahlvorstand(wahlbezirkID)).isNull();
        }

    }

    @Test
    void saveAnwesenheit() {
        val updateData = new WahlvorstandsaktualisierungDTO("id", Collections.emptySet(), LocalDateTime.now());

        unitUnderTest.saveAnwesenheit(updateData);

        Mockito.verify(wahlvorstandService).setAnwesenheit(updateData);
    }

}