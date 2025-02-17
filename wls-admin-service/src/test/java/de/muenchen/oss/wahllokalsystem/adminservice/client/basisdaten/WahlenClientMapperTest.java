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
            val farbeDTO = new Farbe().r(1L).g(2L).b(3L);
            val artDTO = WahlDTO.WahlartEnum.BAW;
            val dtosToMap = List.of(
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(1L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer("1"),
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(2L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer("2"),
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(3L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer("3"));

            dtosToMap.forEach(wahl -> Assertions.assertThat(wahl).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.toModelList(dtosToMap);

            val artModel = WahlartModel.BAW;
            val farbeModel = new FarbeModel(1L, 2L, 3L);
            val expectedModels = List.of(
                    new WahlModel("wahlID1", "name1", 1L, 1L, nowDate, artModel, farbeModel),
                    new WahlModel("wahlID1", "name1", 2L, 1L, nowDate, artModel, farbeModel),
                    new WahlModel("wahlID1", "name1", 3L, 1L, nowDate, artModel, farbeModel));

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
            val artModel = WahlartModel.BAW;
            val farbeModel = new FarbeModel(1L, 2L, 3L);
            val modelsToMap = List.of(
                    new WahlModel("wahlID1", "name1", 1L, 1L, nowDate, artModel, farbeModel),
                    new WahlModel("wahlID1", "name1", 2L, 1L, nowDate, artModel, farbeModel),
                    new WahlModel("wahlID1", "name1", 3L, 1L, nowDate, artModel, farbeModel));

            modelsToMap.forEach(wahl -> Assertions.assertThat(wahl).hasNoNullFieldsOrProperties());

            val result = unitUnderTest.toDtoList(modelsToMap);

            val farbeDTO = new Farbe().r(1L).g(2L).b(3L);
            val artDTO = WahlDTO.WahlartEnum.BAW;
            val expectedDtos = List.of(
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(1L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer(null),
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(2L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer(null),
                    new WahlDTO().wahlID("wahlID1").name("name1").reihenfolge(3L).waehlerverzeichnisnummer(1L).wahltag(nowDate).wahlart(artDTO).farbe(farbeDTO)
                            .nummer(null));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedDtos);
        }
    }
}
