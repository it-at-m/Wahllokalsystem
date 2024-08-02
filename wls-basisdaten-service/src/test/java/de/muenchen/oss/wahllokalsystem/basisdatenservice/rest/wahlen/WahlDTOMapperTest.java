package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage.WahltageDTOMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlDTOMapperTest {

    private final WahlDTOMapper unitUnderTest = Mappers.getMapper(WahlDTOMapper.class);

    @Nested
    class FromListOfWahlModelToListOfWahlDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromListOfWahlModelToListOfWahlDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {
            val modelsInput = createListOfWahlModels();
            val dtosExpected = createControllerListOfWahlDTO();

            val result = unitUnderTest.fromListOfWahlModelToListOfWahlDTO(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }

        @Test
        void isMappedToModel() {
            val modelsExpected = createListOfWahlModels();
            val dtosInput = createControllerListOfWahlDTO();

            val result = unitUnderTest.fromListOfWahlDTOtoListOfWahlModel(dtosInput);
            Assertions.assertThat(result).isEqualTo(modelsExpected);
        }

        private List<WahlDTO> createControllerListOfWahlDTO(){
            val wahl1 = new WahlDTO("wahlID1", "name1", 3L, 1L, LocalDate.now(), Wahlart.BAW, new Farbe(1, 1, 1), "1");
            val wahl2 = new WahlDTO("wahlID2", "name2", 3L, 1L, LocalDate.now(), Wahlart.BAW, new Farbe(1, 1, 1), "2");
            val wahl3 = new WahlDTO("wahlID3", "name3", 3L, 1L, LocalDate.now().plusMonths(2), Wahlart.BAW, new Farbe(1, 1, 1), "3");

            return List.of(wahl1, wahl2, wahl3);
        }

        private List<WahlModel> createListOfWahlModels() {
            val wahl1 = new WahlModel("wahlID1", "name1", 3L, 1L, LocalDate.now(), Wahlart.BAW, new Farbe(1, 1, 1), "1");
            val wahl2 = new WahlModel("wahlID2", "name2", 3L, 1L, LocalDate.now(), Wahlart.BAW, new Farbe(1, 1, 1), "2");
            val wahl3 = new WahlModel("wahlID3", "name3", 3L, 1L, LocalDate.now().plusMonths(2), Wahlart.BAW, new Farbe(1, 1, 1), "3");

            return List.of(wahl1, wahl2, wahl3);
        }
    }
}
