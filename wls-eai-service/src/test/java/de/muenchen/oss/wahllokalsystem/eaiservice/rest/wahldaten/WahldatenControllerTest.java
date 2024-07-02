package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
class WahldatenControllerTest {

    @Mock
    WahldatenService wahldatenService;

    @InjectMocks
    WahldatenController unitUnderTest;

    @Nested
    class LoadWahlberechtigte {

        @Test
        void serviceIsCalled() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedServiceResponse = List.of(WahlberechtigteDTO.builder().build());
            Mockito.when(wahldatenService.getWahlberechtigte(wahlbezirkID)).thenReturn(mockedServiceResponse);

            Assertions.assertThat(unitUnderTest.loadWahlberechtigte(wahlbezirkID)).isSameAs(mockedServiceResponse);
        }

    }

    @Nested
    class LoadWahltageSinceIncluding {
        @Test
        void serviceIsCalled() {
            val includingSince = LocalDate.now();

            val mockedServiceResponse = Set.of(WahltagDTO.builder().build());
            Mockito.when(wahldatenService.getWahltage(eq(includingSince))).thenReturn(mockedServiceResponse);

            Assertions.assertThat(unitUnderTest.loadWahltageSinceIncluding(includingSince)).isSameAs(mockedServiceResponse);
        }
    }

    @Nested
    class LoadWahlbezirke {

        @Test
        void serviceIsCalled() {
            val forDate = LocalDate.now();
            val nummer = "nummer";

            val mockedServiceResponse = Set.of(WahlbezirkDTO.builder().build());
            Mockito.when(wahldatenService.getWahlbezirke(eq(forDate), eq(nummer))).thenReturn(mockedServiceResponse);

            Assertions.assertThat(unitUnderTest.loadWahlbezirke(forDate, nummer)).isSameAs(mockedServiceResponse);
        }

    }

    @Nested
    class LoadWahlen {

        @Test
        void serviceIsCalled() {
            val forDate = LocalDate.now();
            val nummer = "nummer";

            val mockedServiceResponse = Set.of(WahlDTO.builder().build());
            Mockito.when(wahldatenService.getWahlen(eq(forDate), eq(nummer))).thenReturn(mockedServiceResponse);

            Assertions.assertThat(unitUnderTest.loadWahlen(forDate, nummer)).isSameAs(mockedServiceResponse);
        }
    }

    @Nested
    class LoadBasisdaten {

        @Test
        void serviceIsCalled() {
            val forDate = LocalDate.now();
            val nummer = "nummer";

            val mockedServicResponse = new BasisdatenDTO(null, null, null, null);
            Mockito.when(wahldatenService.getBasisdaten(eq(forDate), eq(nummer))).thenReturn(mockedServicResponse);

            Assertions.assertThat(unitUnderTest.loadBasisdaten(forDate, nummer)).isSameAs(mockedServicResponse);
        }
    }
}