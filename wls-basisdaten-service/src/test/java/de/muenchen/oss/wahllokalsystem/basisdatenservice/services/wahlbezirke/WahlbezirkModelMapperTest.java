package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbezirkModelMapperTest {

    private final WahlbezirkModelMapper unitUnderTest = Mappers.getMapper(WahlbezirkModelMapper.class);

    @Test
    void fromListOfWahlbezirkModelToListOfWahlbezirkEntities() {
        val modelsToMap = MockDataFactory.createListOfWahlbezirkModel("", LocalDate.now());

        val result = unitUnderTest.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(modelsToMap);

        val expectedResult = MockDataFactory.createListOfWahlbezirkEntity("", LocalDate.now());
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void fromListOfWahlbezirkEntityToListOfWahlbezirkModel() {
        val entitiesToMap = MockDataFactory.createListOfWahlbezirkEntity("", LocalDate.now());

        val result = unitUnderTest.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(entitiesToMap);

        val expectedResult = MockDataFactory.createListOfWahlbezirkModel("", LocalDate.now());
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }
}
