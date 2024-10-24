package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeit;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { UrnenwahlSchliessungsUhrzeitModelMapperImpl.class })
class UrnenwahlSchliessungsUhrzeitModelMapperTest {

    @Autowired
    private UrnenwahlSchliessungsUhrzeitModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val schliessungsuhrzeit = LocalDateTime.now();
            val entityToMap = new UrnenwahlSchliessungsUhrzeit(wahlbezirkID, schliessungsuhrzeit);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedResult = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, schliessungsuhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val schliessungsuhrzeit = LocalDateTime.now();

            val modelToMap = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, schliessungsuhrzeit);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new UrnenwahlSchliessungsUhrzeit(wahlbezirkID, schliessungsuhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
