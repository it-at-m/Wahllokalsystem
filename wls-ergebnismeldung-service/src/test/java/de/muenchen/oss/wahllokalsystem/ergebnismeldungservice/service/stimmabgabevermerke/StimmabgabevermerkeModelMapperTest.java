package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
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

            val wahldaten1ID = UUID.randomUUID();
            val wahldaten2ID = UUID.randomUUID();
            val vermerk11ID = UUID.randomUUID();
            val vermerk12ID = UUID.randomUUID();
            val vermerk21ID = UUID.randomUUID();
            val vermerk22ID = UUID.randomUUID();

            val wahldaten = Set.of(
                    new Wahldaten(wahldaten1ID, new BezirkUndWahlIDUndWaehlerverzeichnisnummer("wbz1", "wahl1", 1L), Set.of(new Vermerk(vermerk11ID, 1, Set.of(
                            new Stimmzettel(111L, Stimmzettelart.KLEIN), new Stimmzettel(112L, Stimmzettelart.GROSS))), new Vermerk(vermerk12ID, 2,
                                    Set.of(
                                            new Stimmzettel(121L, Stimmzettelart.KLEIN), new Stimmzettel(122L, Stimmzettelart.GROSS)))),
                            Set.of(new EingenommenerWahlscheine(11, Stimmzettelart.KLEIN), new EingenommenerWahlscheine(12, Stimmzettelart.GROSS))),
                    new Wahldaten(wahldaten2ID, new BezirkUndWahlIDUndWaehlerverzeichnisnummer("wbz1", "wahl2", 1L), Set.of(new Vermerk(vermerk21ID, 1, Set.of(
                            new Stimmzettel(211L, Stimmzettelart.KLEIN), new Stimmzettel(212L, Stimmzettelart.GROSS))), new Vermerk(vermerk22ID, 2,
                                    Set.of(
                                            new Stimmzettel(221L, Stimmzettelart.KLEIN), new Stimmzettel(222L, Stimmzettelart.GROSS)))),
                            Set.of(new EingenommenerWahlscheine(21, Stimmzettelart.KLEIN), new EingenommenerWahlscheine(22, Stimmzettelart.GROSS))));

            val entityToMap = new Stimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                    wahldaten);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedWahldaten = Set.of(
                    new WahldatenModel("wbz1", "wahl1", 1L,
                            Set.of(new VermerkModel(1, Set.of(
                                    new StimmzettelModel(111L, StimmzettelartModel.KLEIN), new StimmzettelModel(112L, StimmzettelartModel.GROSS))),
                                    new VermerkModel(2, Set.of(
                                            new StimmzettelModel(121L, StimmzettelartModel.KLEIN), new StimmzettelModel(122L, StimmzettelartModel.GROSS)))),
                            Set.of(new EingenommenerWahlscheinModel(11, StimmzettelartModel.KLEIN),
                                    new EingenommenerWahlscheinModel(12, StimmzettelartModel.GROSS))),
                    new WahldatenModel("wbz1", "wahl2", 1L,
                            Set.of(new VermerkModel(1, Set.of(
                                    new StimmzettelModel(211L, StimmzettelartModel.KLEIN), new StimmzettelModel(212L, StimmzettelartModel.GROSS))),
                                    new VermerkModel(2, Set.of(
                                            new StimmzettelModel(221L, StimmzettelartModel.KLEIN), new StimmzettelModel(222L, StimmzettelartModel.GROSS)))),
                            Set.of(new EingenommenerWahlscheinModel(21, StimmzettelartModel.KLEIN),
                                    new EingenommenerWahlscheinModel(22, StimmzettelartModel.GROSS))));
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

            val mappedStimmzettelartModel = result.wahldaten().iterator().next().vermerke().iterator().next().stimmzetteln().iterator().next().stimmzettelart();
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
                        new WahldatenModel("wbz1", "wahl1", 1L,
                                Set.of(new VermerkModel(1, Set.of(
                                        new StimmzettelModel(111L, StimmzettelartModel.KLEIN), new StimmzettelModel(112L, StimmzettelartModel.GROSS))),
                                        new VermerkModel(2, Set.of(
                                                new StimmzettelModel(121L, StimmzettelartModel.KLEIN), new StimmzettelModel(122L, StimmzettelartModel.GROSS)))),
                                Set.of(new EingenommenerWahlscheinModel(11, StimmzettelartModel.KLEIN),
                                        new EingenommenerWahlscheinModel(12, StimmzettelartModel.GROSS))),
                        new WahldatenModel("wbz1", "wahl2", 1L,
                                Set.of(new VermerkModel(1, Set.of(
                                        new StimmzettelModel(211L, StimmzettelartModel.KLEIN), new StimmzettelModel(212L, StimmzettelartModel.GROSS))),
                                        new VermerkModel(2, Set.of(
                                                new StimmzettelModel(221L, StimmzettelartModel.KLEIN), new StimmzettelModel(222L, StimmzettelartModel.GROSS)))),
                                Set.of(new EingenommenerWahlscheinModel(21, StimmzettelartModel.KLEIN),
                                        new EingenommenerWahlscheinModel(22, StimmzettelartModel.GROSS))));
                val given = new StimmabgabevermerkeModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisnummer), anzahlBlaetter,
                        givenWahldaten);

                val result = unitUnderTest.toEntity(given);

                val expectedWahldaten = Set.of(
                        new Wahldaten(null, new BezirkUndWahlIDUndWaehlerverzeichnisnummer("wbz1", "wahl1", 1L), Set.of(new Vermerk(null, 1, Set.of(
                                new Stimmzettel(111L, Stimmzettelart.KLEIN), new Stimmzettel(112L, Stimmzettelart.GROSS))), new Vermerk(null, 2,
                                        Set.of(
                                                new Stimmzettel(121L, Stimmzettelart.KLEIN), new Stimmzettel(122L, Stimmzettelart.GROSS)))),
                                Set.of(new EingenommenerWahlscheine(11, Stimmzettelart.KLEIN), new EingenommenerWahlscheine(12, Stimmzettelart.GROSS))),
                        new Wahldaten(null, new BezirkUndWahlIDUndWaehlerverzeichnisnummer("wbz1", "wahl2", 1L), Set.of(new Vermerk(null, 1, Set.of(
                                new Stimmzettel(211L, Stimmzettelart.KLEIN), new Stimmzettel(212L, Stimmzettelart.GROSS))), new Vermerk(null, 2,
                                        Set.of(
                                                new Stimmzettel(221L, Stimmzettelart.KLEIN), new Stimmzettel(222L, Stimmzettelart.GROSS)))),
                                Set.of(new EingenommenerWahlscheine(21, Stimmzettelart.KLEIN), new EingenommenerWahlscheine(22, Stimmzettelart.GROSS))));
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

                Assertions.assertThat(result.getStimmzetteln().iterator().next().getStimmzettelart().name()).isEqualTo(stimmzettelartModel.name());
            }
        }
    }

}
