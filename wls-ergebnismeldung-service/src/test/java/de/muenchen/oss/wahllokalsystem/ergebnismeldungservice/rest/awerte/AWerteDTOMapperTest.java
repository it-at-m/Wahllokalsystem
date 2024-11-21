package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AWerteDTOMapperTest {

    private final AWerteDTOMapper unitUnderTest = Mappers.getMapper(AWerteDTOMapper.class);

    @Nested
    class FromListOfAWerteModelToListOfAWerteDTO {

        @Test
        void should_returnNull_whenNullIsGiven() {
            Assertions.assertThat(unitUnderTest.fromListOfAWerteModelToListOfAWerteDTO(null)).isNull();
        }

        @Test
        void should_returnListOfAWerteDTO_when_listOfAWerteModelIsGiven() {
            val modelsInput = createListOfAWerteModels();
            val dtosExpected = createListOfAWerteDTO();

            val result = unitUnderTest.fromListOfAWerteModelToListOfAWerteDTO(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }

        private List<AWerteDTO> createListOfAWerteDTO() {
            val aWert1 = new AWerteDTO("wahlID1", "wahlbezirkID", 2, 3L);
            val aWert2 = new AWerteDTO("wahlID2", "wahlbezirkID", 4, 5L);
            val aWert3 = new AWerteDTO("wahlID3", "wahlbezirkID", 5, 6L);
            return List.of(aWert1, aWert2, aWert3);
        }

        private List<AWerteModel> createListOfAWerteModels() {
            val aWert1 = new AWerteModel(new BezirkUndWahlID("wahlID1", "wahlbezirkID"), 2, 3L);
            val aWert2 = new AWerteModel(new BezirkUndWahlID("wahlID2", "wahlbezirkID"), 4, 5L);
            val aWert3 = new AWerteModel(new BezirkUndWahlID("wahlID3", "wahlbezirkID"), 5, 6L);
            return List.of(aWert1, aWert2, aWert3);
        }
    }
}
