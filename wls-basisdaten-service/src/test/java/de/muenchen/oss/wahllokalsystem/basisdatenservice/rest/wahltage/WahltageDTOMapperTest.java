package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahltageDTOMapperTest {

    private final WahltageDTOMapper unitUnderTest = Mappers.getMapper(WahltageDTOMapper.class);

    @Nested
    class FromListOfWahltagModelToListOfWahltagDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromListOfWahltagModelToListOfWahltagDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {

            val modelsInput = createClientWahltagModels();
            val dtosExpected = createClientWahltagDTOs();

            val result = unitUnderTest.fromListOfWahltagModelToListOfWahltagDTO(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }

        private List<WahltagDTO> createClientWahltagDTOs() {

            val wahltag1 = new WahltagDTO("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
            val wahltag2 = new WahltagDTO("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
            val wahltag3 = new WahltagDTO("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

            val wahltageDTOs = List.of(wahltag1, wahltag2, wahltag3);

            return wahltageDTOs;
        }

        private List<WahltagModel> createClientWahltagModels() {

            val wahltag1 = new WahltagModel("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
            val wahltag2 = new WahltagModel("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
            val wahltag3 = new WahltagModel("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

            val wahltagModels = List.of(wahltag1, wahltag2, wahltag3);

            return wahltagModels;
        }
    }
}
