package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

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

class WahlDTOMapperTest {

    private final WahlDTOMapper unitUnderTest = Mappers.getMapper(WahlDTOMapper.class);

    @Nested
    class ToDtoList {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDtoList(null)).isNull();
        }

        @Test
        void should_returnDtoList_when_givenModelList() {
            val modelsInput = createWahlModelList();
            val dtosExpected = createWahlDTOList();

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
            val modelsExpected = createWahlModelList();
            val dtosInput = createWahlDTOList();

            val result = unitUnderTest.toModelList(dtosInput);
            Assertions.assertThat(result).isEqualTo(modelsExpected);
        }
    }

    private List<WahlDTO> createWahlDTOList() {
        val wahl1 = new WahlDTO("wahlID1", "name1", 3L, 1L, LocalDate.now(), WahlartDTO.BAW, new FarbeDTO(1, 2, 3));
        val wahl2 = new WahlDTO("wahlID2", "name2", 3L, 1L, LocalDate.now().plusMonths(1), WahlartDTO.BTW, new FarbeDTO(4, 5, 6));
        val wahl3 = new WahlDTO("wahlID3", "name3", 3L, 1L, LocalDate.now().plusMonths(2), WahlartDTO.LTW, new FarbeDTO(7, 8, 9));

        return List.of(wahl1, wahl2, wahl3);
    }

    private List<WahlModel> createWahlModelList() {
        val wahl1 = new WahlModel("wahlID1", "name1", 3L, 1L, LocalDate.now(), WahlartModel.BAW, new FarbeModel(1, 2, 3));
        val wahl2 = new WahlModel("wahlID2", "name2", 3L, 1L, LocalDate.now().plusMonths(1), WahlartModel.BTW, new FarbeModel(4, 5, 6));
        val wahl3 = new WahlModel("wahlID3", "name3", 3L, 1L, LocalDate.now().plusMonths(2), WahlartModel.LTW, new FarbeModel(7, 8, 9));

        return List.of(wahl1, wahl2, wahl3);
    }
}
