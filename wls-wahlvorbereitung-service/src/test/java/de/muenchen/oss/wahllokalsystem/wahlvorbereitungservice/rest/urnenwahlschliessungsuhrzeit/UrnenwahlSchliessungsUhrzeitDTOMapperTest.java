package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitModel;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { UrnenwahlSchliessungsUhrzeitDTOMapperImpl.class })
class UrnenwahlSchliessungsUhrzeitDTOMapperTest {

    @Autowired
    private UrnenwahlSchliessungsUhrzeitDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val urnenwahlSchliessungsUhrzeit = LocalDateTime.now();
            val modelToMap = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, urnenwahlSchliessungsUhrzeit);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new UrnenwahlSchliessungsUhrzeitDTO(wahlbezirkID, urnenwahlSchliessungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val urnenwahlSchliessungsUhrzeit = LocalDateTime.now();
            val dtoToMap = new UrnenwahlSchliessungsUhrzeitWriteDTO(urnenwahlSchliessungsUhrzeit);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkIDToMap, urnenwahlSchliessungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}