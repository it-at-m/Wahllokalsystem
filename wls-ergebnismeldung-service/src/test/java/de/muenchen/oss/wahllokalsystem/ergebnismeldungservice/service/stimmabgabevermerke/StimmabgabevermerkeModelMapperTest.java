package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class StimmabgabevermerkeModelMapperTest {

    final StimmabgabevermerkeModelMapper unitUnderTest = Mappers.getMapper(StimmabgabevermerkeModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toModel((Stimmabgabevermerke) null)).isNull();
        }

        @Test
        void should_returnModel_when_entityIsGiven() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisnummer = 0L;
            val anzahlBlaetter = 12L;

            val wahldaten = Set.of(
                    Testdaten.Wahldaten.createEntity("wbz1", "wahl1", 1L),
                    Testdaten.Wahldaten.createEntity("wbz1", "wahl2", 2L));

            val entityToMap = new Stimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                    wahldaten);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedWahldaten = Set.of(
                    Testdaten.Wahldaten.createModel("wbz1", "wahl1", 1L),
                    Testdaten.Wahldaten.createModel("wbz1", "wahl2", 2L));
            val expectedResult = new StimmabgabevermerkeModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                    expectedWahldaten);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Stimmzettelart.class)
        void should_useStimmzettelartWithSameName_when_stimmzettelartModelIsGiven(final Stimmzettelart stimmzettelart) {
            val entityToMap = new Stimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer("", 0L), 0L,
                    Set.of(new Wahldaten(null, null, Set.of(new Vermerk(UUID.randomUUID(), 0L, Set.of(new Stimmzettel(
                            0L, stimmzettelart)))), Collections.emptySet())));

            val result = unitUnderTest.toModel(entityToMap);

            val mappedStimmzettelartModel = result.wahldaten().iterator().next().vermerke().iterator().next().stimmzettel().iterator().next().stimmzettelart();
            Assertions.assertThat(mappedStimmzettelartModel.name())
                    .isEqualTo(stimmzettelart.name());
        }
    }

    @Nested
    class ToEntity {

        @Nested
        class OfStimmabgabevermerkeModel {

            @Test
            void should_returnNull_when_nullIsGiven() {
                Assertions.assertThat(unitUnderTest.toEntity((StimmabgabevermerkeModel) null)).isNull();
            }

            @Test
            void should_returnModel_when_entityIsGiven() {
                val wahlbezirkID = "wahlbezirkID";
                val waehlerverzeichnisnummer = 0L;
                val anzahlBlaetter = 12L;

                val givenWahldaten = Set.of(
                        Testdaten.Wahldaten.createModel("wbz1", "wahl1", 1L),
                        Testdaten.Wahldaten.createModel("wbz1", "wahl2", 2L));
                val given = new StimmabgabevermerkeModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                        givenWahldaten);

                val result = unitUnderTest.toEntity(given);

                val expectedWahldaten = Set.of(
                        Testdaten.Wahldaten.createEntity("wbz1", "wahl1", 1L),
                        Testdaten.Wahldaten.createEntity("wbz1", "wahl2", 2L));
                val expectedResult = new Stimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                        expectedWahldaten);

                Assertions.assertThat(result).isEqualTo(expectedResult);
            }

            @ParameterizedTest
            @EnumSource(StimmzettelartModel.class)
            void should_useStimmzettelartWithSameName_when_stimmzettelartModelIsGiven(final StimmzettelartModel stimmzettelartModel) {
                val vermerkToMap = new VermerkModel(0L, Set.of(new StimmzettelModel(
                        0L, stimmzettelartModel)));

                val result = unitUnderTest.toEntity(vermerkToMap);

                Assertions.assertThat(result.getStimmzettel().iterator().next().getStimmzettelart().name()).isEqualTo(stimmzettelartModel.name());
            }
        }
    }

}
