package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.FortsetzungsUhrzeit;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { FortsetzungsUhrzeitModelMapperImpl.class })
class FortsetzungsUhrzeitModelMapperTest {

    @Autowired
    private FortsetzungsUhrzeitModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val fortsetzungsUhrzeit = LocalDateTime.now();
            val entityToMap = new FortsetzungsUhrzeit(wahlbezirkID, fortsetzungsUhrzeit);

            val result = unitUnderTest.toModel(entityToMap);

            val expecetedResult = new FortsetzungsUhrzeitModel(wahlbezirkID, fortsetzungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val fortsetzungsUhrzeit = LocalDateTime.now();

            val modelToMap = new FortsetzungsUhrzeitModel(wahlbezirkID, fortsetzungsUhrzeit);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new FortsetzungsUhrzeit(wahlbezirkID, fortsetzungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
