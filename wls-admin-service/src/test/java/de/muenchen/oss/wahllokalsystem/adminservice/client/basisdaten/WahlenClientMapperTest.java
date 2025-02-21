package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.Farbe;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.FarbeModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlartModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlenClientMapperTest {

    private final WahlenClientMapper unitUnderTest = Mappers.getMapper(WahlenClientMapper.class);

    @Nested
    class ToModelList {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toModelList(null)).isNull();
        }

        @Test
        void should_mapToListOfModel_when_givenListOfDTO() {
            val nowDate = LocalDate.now();
            val dtosToMap = List.of(
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(1L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BAW)
                            .farbe(new Farbe().r(1L).g(2L).b(3L))
                            .nummer("1"),
                    new WahlDTO().wahlID("wahlID2").name("name2").reihenfolge(2L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BTW)
                            .farbe(new Farbe().r(4L).g(5L).b(6L))
                            .nummer("2"),
                    new WahlDTO().wahlID("wahlID3").name("name3").reihenfolge(3L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.LTW)
                            .farbe(new Farbe().r(7L).g(8L).b(9L))
                            .nummer("3"));

            Assertions.assertThat(dtosToMap).allSatisfy(wahl -> Assertions.assertThat(wahl).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.toModelList(dtosToMap);

            val expectedModels = List.of(
                    new WahlModel("wahlID1", "name1", 1L, 1L, nowDate, WahlartModel.BAW, new FarbeModel(1L, 2L, 3L)),
                    new WahlModel("wahlID2", "name2", 2L, 1L, nowDate, WahlartModel.BTW, new FarbeModel(4L, 5L, 6L)),
                    new WahlModel("wahlID3", "name3", 3L, 1L, nowDate, WahlartModel.LTW, new FarbeModel(7L, 8L, 9L)));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedModels);
        }
    }

    @Nested
    class ToDtoList {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toDtoList(null)).isNull();
        }

        @Test
        void should_mapToListOfDto_when_givenListOfModel() {
            val nowDate = LocalDate.now();
            val modelsToMap = List.of(
                    new WahlModel("wahlID1", "name1", 1L, 1L, nowDate, WahlartModel.BAW, new FarbeModel(1L, 2L, 3L)),
                    new WahlModel("wahlID2", "name2", 2L, 1L, nowDate, WahlartModel.BTW, new FarbeModel(4L, 5L, 6L)),
                    new WahlModel("wahlID3", "name3", 3L, 1L, nowDate, WahlartModel.LTW, new FarbeModel(7L, 8L, 9L)));

            modelsToMap.forEach(wahl -> Assertions.assertThat(wahl).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.toDtoList(modelsToMap);

            val expectedDtos = List.of(
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(1L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BAW)
                            .farbe(new Farbe().r(1L).g(2L).b(3L))
                            .nummer(null),
                    new WahlDTO().wahlID("wahlID2").name("name2").reihenfolge(2L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BTW)
                            .farbe(new Farbe().r(4L).g(5L).b(6L))
                            .nummer(null),
                    new WahlDTO().wahlID("wahlID3").name("name3").reihenfolge(3L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.LTW)
                            .farbe(new Farbe().r(7L).g(8L).b(9L))
                            .nummer(null));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedDtos);
        }
    }
}
