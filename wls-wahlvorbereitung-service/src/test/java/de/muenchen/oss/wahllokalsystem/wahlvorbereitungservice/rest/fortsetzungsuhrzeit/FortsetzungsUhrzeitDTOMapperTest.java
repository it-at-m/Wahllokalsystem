package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitModel;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { FortsetzungsUhrzeitDTOMapperImpl.class })
class FortsetzungsUhrzeitDTOMapperTest {

    @Autowired
    private FortsetzungsUhrzeitDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val fortsetzungsUhrzeit = LocalDateTime.now();
            val modelToMap = new FortsetzungsUhrzeitModel(wahlbezirkID, fortsetzungsUhrzeit);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new FortsetzungsUhrzeitDTO(wahlbezirkID, fortsetzungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val fortsetzungsUhrzeit = LocalDateTime.now();
            val dtoToMap = new FortsetzungsUhrzeitWriteDTO(fortsetzungsUhrzeit);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new FortsetzungsUhrzeitModel(wahlbezirkIDToMap, fortsetzungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
