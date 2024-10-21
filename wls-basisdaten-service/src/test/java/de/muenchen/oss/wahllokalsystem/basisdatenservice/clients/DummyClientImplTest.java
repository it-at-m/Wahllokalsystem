package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.StimmzettelgebietModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DummyClientImplTest {

    private final DummyClientImpl unitUnderTest = new DummyClientImpl();

    @Nested
    class GetWahlvorschlaege {

        @Test
        void resultIsANonNullObject() {
            val result = unitUnderTest.getWahlvorschlaege(new BezirkUndWahlID("wahlID", "wahlbezirkID"));

            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    class GetWahltage {

        @Test
        void resultIsAnArrayWithPositiveSize() {
            val result = unitUnderTest.getWahltage(LocalDate.now().minusMonths(3));
            Assertions.assertThat(result).size().isPositive();
        }
    }

    @Nested
    class GetWahlen {

        @Test
        void resultIsAnArrayWithPositiveSize() {
            val result = unitUnderTest.getWahlen(new WahltagWithNummer(LocalDate.now(), "0"));
            Assertions.assertThat(result).size().isPositive();
        }
    }

    @Nested
    class LoadBasisdaten {

        @Test
        void resultHasRightTypeAndAllPropertiesContainingData() {
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0"));
            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
            Assertions.assertThat(result.basisstrukturdaten()).isNotEmpty();
            Assertions.assertThat(result.wahlen()).isNotEmpty();
            Assertions.assertThat(result.wahlbezirke()).isNotEmpty();
            Assertions.assertThat(result.stimmzettelgebiete()).isNotEmpty();
        }

        @Test
        void forEveryBasistrutkturdatenCorespondingAtLeastOneWahlOneWahlbezirkAndOneStimmzettelgebiet() {
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0"));
            result.basisstrukturdaten().forEach((bsd) -> {
                Assertions.assertThat(result.wahlen()).anyMatch(w -> w.wahlID().equals(bsd.wahlID()));
                Assertions.assertThat(result.wahlbezirke()).anyMatch(wbz -> wbz.wahlbezirkID().equals(bsd.wahlbezirkID()));
                Assertions.assertThat(result.stimmzettelgebiete()).anyMatch(szg -> szg.identifikator().equals(bsd.stimmzettelgebietID()));
            });
        }

        @Test
        void forEveryWahlCorespondingAtLeastOneBasistrutkturdatenAndOneWahlbezirk() {
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0"));
            result.wahlen().forEach((wahl) -> {
                Assertions.assertThat(result.basisstrukturdaten()).anyMatch(bsd -> bsd.wahlID().equals(wahl.wahlID()));
                Assertions.assertThat(result.wahlbezirke()).anyMatch(wbz -> wbz.wahlID().equals(wahl.wahlID()));
            });
        }

        @Test
        void forEveryWahlbezirkCorespondingAtLeastOneBasistrutkturdatenAndOneWahl() {
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0"));
            result.wahlbezirke().forEach((wbz) -> {
                Assertions.assertThat(result.basisstrukturdaten()).anyMatch(bsd -> bsd.wahlbezirkID().equals(wbz.wahlbezirkID()));
                Assertions.assertThat(result.wahlen()).anyMatch(wahl -> wahl.wahlID().equals(wbz.wahlID()));
            });
        }

        @Test
        void forEveryStimmzettelgebietCorespondingAtLeastOneBasistrutkturdaten() {
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(LocalDate.now(), "0"));
            for (StimmzettelgebietModel szg : result.stimmzettelgebiete()) {
                Assertions.assertThat(result.basisstrukturdaten()).anyMatch(bsd -> bsd.stimmzettelgebietID().equals(szg.identifikator()));
            }
        }

        @Test
        void allUnderobjectsHaveRequestedDate() {
            val aDate = LocalDate.now();
            val result = unitUnderTest.loadBasisdaten(new WahltagWithNummer(aDate, "0"));
            Assertions.assertThat(result.basisstrukturdaten()).allMatch(bsd -> bsd.wahltag().equals(aDate));
            Assertions.assertThat(result.wahlen()).allMatch(w -> w.wahltag().equals(aDate));
            Assertions.assertThat(result.wahlbezirke()).allMatch(wbz -> wbz.wahltag().equals(aDate));
            Assertions.assertThat(result.stimmzettelgebiete()).allMatch(szg -> szg.wahltag().equals(aDate));
        }
    }

    @Nested
    class GetKonfigurierterWahltag {

        @Test
        void resultIsANonNullObjectWithActivePropertyTrue() {
            val result = unitUnderTest.getKonfigurierterWahltag();
            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
            Assertions.assertThat(result.active()).isTrue();
        }
    }

    @Nested
    class LoadWahlbezirke {

        @Test
        void resultIsAnArrayWithPositiveSize() {
            val result = unitUnderTest.loadWahlbezirke(LocalDate.now(), "0");
            Assertions.assertThat(result).isNotEmpty();
        }
    }
}
