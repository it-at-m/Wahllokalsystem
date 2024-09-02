package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbezirkDTOMapperTest {

    private final WahlbezirkDTOMapper unitUnderTest = Mappers.getMapper(WahlbezirkDTOMapper.class);

    @Nested
    class FromListOfWahlbezirkModelToListOfWahlbezirkDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {
            val modelsInput = MockDataFactory.createListOfWahlbezirkModel("", LocalDate.now());
            val dtosExpected = MockDataFactory.createWlsWahlbezirkDTOs("", LocalDate.now());

            val result = unitUnderTest.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }
    }
}
