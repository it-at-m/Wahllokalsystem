package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AWerteModelMapperTest {

    private final AWerteModelMapper unitUnderTest = Mappers.getMapper(AWerteModelMapper.class);

    @Test
    void should_returnListOfAWerteEntity_when_listOfAWerteModelIsGiven() {
        val wahlbezirkID = "wahlbezirkID";
        val modelsToMap = createListOfAWerteModels(wahlbezirkID);

        val result = unitUnderTest.fromListOfAWerteModeltoListOfAWerteEntity(modelsToMap);

        val expectedResult = createListOfAWerteEntities(wahlbezirkID);
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void should_return_listOfAWerteModel_when_listOfAWerteEntityIsGiven() {
        val wahlbezirkID = "wahlbezirkID";
        val entitiesToMap = createListOfAWerteEntities(wahlbezirkID);

        val result = unitUnderTest.fromListOfAWerteEntityToListOfAWerteModel(entitiesToMap);

        val expectedResult = createListOfAWerteModels(wahlbezirkID);
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private List<AWerte> createListOfAWerteEntities(String wahlbezirkID) {
        val aWert1 = new AWerte(new BezirkUndWahlID("wahlID1", wahlbezirkID), 2, 3L);
        val aWert2 = new AWerte(new BezirkUndWahlID("wahlID2", wahlbezirkID), 4, 5L);
        val aWert3 = new AWerte(new BezirkUndWahlID("wahlID3", wahlbezirkID), 5, 6L);
        return List.of(aWert1, aWert2, aWert3);
    }

    private List<AWerteModel> createListOfAWerteModels(String wahlbezirkID) {
        val aWert1 = new AWerteModel(new BezirkUndWahlID("wahlID1", wahlbezirkID), 2, 3L);
        val aWert2 = new AWerteModel(new BezirkUndWahlID("wahlID2", wahlbezirkID), 4, 5L);
        val aWert3 = new AWerteModel(new BezirkUndWahlID("wahlID3", wahlbezirkID), 5, 6L);
        return List.of(aWert1, aWert2, aWert3);
    }
}
