package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModel;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { UnterbrechungsUhrzeitDTOMapperImpl.class })
class UnterbrechungsUhrzeitDTOMapperTest {

    @Autowired
    private UnterbrechungsUhrzeitDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val unterbrechungsUhrzeit = LocalDateTime.now();
            val modelToMap = new UnterbrechungsUhrzeitModel(wahlbezirkID, unterbrechungsUhrzeit);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new UnterbrechungsUhrzeitDTO(wahlbezirkID, unterbrechungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val unterbrechungsUhrzeit = LocalDateTime.now();
            val dtoToMap = new UnterbrechungsUhrzeitWriteDTO(unterbrechungsUhrzeit);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new UnterbrechungsUhrzeitModel(wahlbezirkIDToMap, unterbrechungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
