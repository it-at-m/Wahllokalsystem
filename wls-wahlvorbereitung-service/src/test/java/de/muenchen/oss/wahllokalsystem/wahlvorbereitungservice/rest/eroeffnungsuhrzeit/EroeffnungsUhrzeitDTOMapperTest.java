package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitModel;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { EroeffnungsUhrzeitDTOMapperImpl.class })
class EroeffnungsUhrzeitDTOMapperTest {

    @Autowired
    private EroeffnungsUhrzeitDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val eroeffnungsUhrzeit = LocalDateTime.now();
            val modelToMap = new EroeffnungsUhrzeitModel(wahlbezirkID, eroeffnungsUhrzeit);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new EroeffnungsUhrzeitDTO(wahlbezirkID, eroeffnungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val eroeffnungsUhrzeit = LocalDateTime.now();
            val dtoToMap = new EroeffnungsUhrzeitWriteDTO(eroeffnungsUhrzeit);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new EroeffnungsUhrzeitModel(wahlbezirkIDToMap, eroeffnungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
