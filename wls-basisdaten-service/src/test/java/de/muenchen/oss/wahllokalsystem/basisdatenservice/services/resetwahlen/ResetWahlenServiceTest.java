package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResetWahlenServiceTest {

    @Mock
    WahlRepository wahlRepository;

    @InjectMocks
    ResetWahlenService unitUnderTest;

    @Nested
    class ResetWahlen {

        @Test
        void dataSuccessfullyReseted() {
            ArgumentCaptor<List<Wahl>> reqCaptor = ArgumentCaptor.forClass(List.class);

            val wahlenToReset = createWahlEntities(false);
            Mockito.when(wahlRepository.findAll()).thenReturn(wahlenToReset);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.resetWahlen());

            val resetedWahlen = createWahlEntities(true);

            Mockito.verify(wahlRepository).saveAll(reqCaptor.capture());
            Assertions.assertThat(reqCaptor.getValue()).containsExactlyInAnyOrderElementsOf(resetedWahlen);
        }

        private List<Wahl> createWahlEntities(boolean returnResetedWahlen) {
            val wahl1 = new Wahl();
            wahl1.setWahlID("wahlid1");
            wahl1.setName("wahl1");
            wahl1.setNummer("0");
            wahl1.setFarbe(new Farbe(1, 1, 1));
            wahl1.setWahlart(Wahlart.BAW);
            wahl1.setReihenfolge(1);
            wahl1.setWaehlerverzeichnisnummer(1);
            wahl1.setWahltag(LocalDate.now().plusMonths(1));

            val wahl2 = new Wahl();
            wahl2.setWahlID("wahlid2");
            wahl2.setName("wahl2");
            wahl2.setNummer("1");
            wahl2.setFarbe(new Farbe(2, 2, 2));
            wahl2.setWahlart(Wahlart.LTW);
            wahl2.setReihenfolge(2);
            wahl2.setWaehlerverzeichnisnummer(2);
            wahl2.setWahltag(LocalDate.now().plusMonths(2));

            val wahl3 = new Wahl();
            wahl3.setWahlID("wahlid3");
            wahl3.setName("wahl3");
            wahl3.setNummer("2");
            wahl3.setFarbe(new Farbe(3, 3, 3));
            wahl3.setWahlart(Wahlart.EUW);
            wahl3.setReihenfolge(3);
            wahl3.setWaehlerverzeichnisnummer(3);
            wahl3.setWahltag(LocalDate.now().plusMonths(3));

            return (returnResetedWahlen) ? List.of(resetWahl(wahl1), resetWahl(wahl2), resetWahl(wahl3)) : List.of(wahl1, wahl2, wahl3);
        }

        private Wahl resetWahl(Wahl wahl) {
            wahl.setFarbe(new Farbe(0, 0, 0));
            wahl.setReihenfolge(0);
            wahl.setWaehlerverzeichnisnummer(1);
            return wahl;
        }
    }

}
