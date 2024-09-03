package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlenClientMapperTest {

    private final WahlenClientMapper unitUnderTest = Mappers.getMapper(WahlenClientMapper.class);

    @Nested
    class ToWahlModel {

        @Test
        void isMapped() {
            val aNowMoment = LocalDate.now();
            val wahl1 = new WahlDTO();
            wahl1.setIdentifikator("identifikatorWahl1");
            wahl1.setNummer("nummerWahl1");
            wahl1.setName("nameWahl1");
            wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl1.setWahltag(aNowMoment);

            Assertions.assertThat(wahl1).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(wahl1);

            val expectedWahl = new WahlModel(
                    "identifikatorWahl1",
                    "nameWahl1",
                    1L,
                    1L,
                    aNowMoment,
                    Wahlart.BAW,
                    new Farbe(0, 0, 0),
                    "nummerWahl1");

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedWahl);
        }
    }

    @Nested
    class FromRemoteClientSetOfWahlDTOtoListOfWahlModel {

        @Test
        void isMapped() {
            val wahl1 = new WahlDTO();
            wahl1.setIdentifikator("identifikatorWahl1");
            wahl1.setNummer("nummerWahl1");
            wahl1.setName("nameWahl1");
            wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl1.setWahltag(LocalDate.now());

            val wahl2 = new WahlDTO();
            wahl2.setIdentifikator("identifikatorWahl2");
            wahl2.setNummer("nummerWahl2");
            wahl2.setName("nameWahl2");
            wahl2.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl2.setWahltag(LocalDate.now());

            val wahl3 = new WahlDTO();
            wahl3.setIdentifikator("identifikatorWahl3");
            wahl3.setNummer("nummerWahl3");
            wahl3.setName("nameWahl3");
            wahl3.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl3.setWahltag(LocalDate.now());

            val dtosToMap = Set.of(wahl1, wahl2, wahl3);

            Assertions.assertThat(dtosToMap).size().isEqualTo(3);
            dtosToMap.forEach(wahl -> Assertions.assertThat(wahl).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.fromRemoteClientSetOfWahlDTOtoListOfWahlModel(dtosToMap);

            val expectedWahltage = List.of(
                    new WahlModel(
                            "identifikatorWahl1",
                            "nameWahl1",
                            1L,
                            1L,
                            LocalDate.now(),
                            Wahlart.BAW,
                            new Farbe(0, 0, 0),
                            "nummerWahl1"),
                    new WahlModel(
                            "identifikatorWahl2",
                            "nameWahl2",
                            1L,
                            1L,
                            LocalDate.now(),
                            Wahlart.BAW,
                            new Farbe(0, 0, 0),
                            "nummerWahl2"),
                    new WahlModel(
                            "identifikatorWahl3",
                            "nameWahl3",
                            1L,
                            1L,
                            LocalDate.now(),
                            Wahlart.BAW,
                            new Farbe(0, 0, 0),
                            "nummerWahl3"));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWahltage);
        }
    }
}
