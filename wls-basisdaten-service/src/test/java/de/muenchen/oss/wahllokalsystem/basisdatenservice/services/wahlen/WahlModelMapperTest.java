package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlModelMapperTest {

    private final WahlModelMapper unitUnderTest = Mappers.getMapper(WahlModelMapper.class);

    @Test
    void fromListOfWahlModeltoListOfWahlEntities() {
        val modelsToMap = createListOfWahlModels();

        val result = unitUnderTest.fromListOfWahlModeltoListOfWahlEntities(modelsToMap);

        val expectedResult = createWahlenList();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void fromListOfWahlEntityToListOfWahlModel() {
        val entitiesToMap = createWahlenList();

        val result = unitUnderTest.fromListOfWahlEntityToListOfWahlModel(entitiesToMap);

        val expectedResult = createListOfWahlModels();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    private List<Wahl> createWahlenList() {
        val wahl1 = new Wahl("wahlID1", "name1", 3L, 1L, LocalDate.now().minusMonths(1), Wahlart.BAW, new Farbe(1, 1, 1), "1");
        val wahl2 = new Wahl("wahlID2", "name2", 2L, 2L, LocalDate.now().plusMonths(1), Wahlart.EUW, new Farbe(2, 2, 2), "2");
        val wahl3 = new Wahl("wahlID3", "name3", 1L, 3L, LocalDate.now().minusMonths(1), Wahlart.VE, new Farbe(3, 3, 3), "3");

        return Arrays.asList(wahl1, wahl2, wahl3);
    }

    private List<WahlModel> createListOfWahlModels() {
        val wahl1 = new WahlModel("wahlID1", "name1", 3L, 1L, LocalDate.now().minusMonths(1), Wahlart.BAW, new Farbe(1, 1, 1), "1");
        val wahl2 = new WahlModel("wahlID2", "name2", 2L, 2L, LocalDate.now().plusMonths(1), Wahlart.EUW, new Farbe(2, 2, 2), "2");
        val wahl3 = new WahlModel("wahlID3", "name3", 1L, 3L, LocalDate.now().minusMonths(1), Wahlart.VE, new Farbe(3, 3, 3), "3");

        return List.of(wahl1, wahl2, wahl3);
    }
}
