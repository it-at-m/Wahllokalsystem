package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahltagDTOMapperTest {

    private final WahltagDTOMapper unitUnderTest = Mappers.getMapper(WahltagDTOMapper.class);

    @Nested
    class ToDtoList {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDtoList(null)).isNull();
        }

        @Test
        void should_returnDtoList_when_givenModelList() {
            val modelsInput = createWahltagModelList();
            val dtosExpected = createWahltagDTOList();

            val result = unitUnderTest.toDtoList(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }
    }

    @Nested
    class ToModelList {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModelList(null)).isNull();
        }

        @Test
        void should_returnModelList_when_givenDtoList() {
            val dtosInput = createWahltagDTOList();
            val modelsExpected = createWahltagModelList();

            val result = unitUnderTest.toModelList(dtosInput);
            Assertions.assertThat(result).isEqualTo(modelsExpected);
        }
    }

    private List<WahltagDTO> createWahltagDTOList() {
        val wahltag1 = new WahltagDTO("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new WahltagDTO("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new WahltagDTO("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }

    private List<WahltagModel> createWahltagModelList() {
        val wahltag1 = new WahltagModel("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new WahltagModel("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new WahltagModel("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }
}
