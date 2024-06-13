package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { UnterbrechungsUhrzeitModelMapperImpl.class })
class UnterbrechungsUhrzeitModelMapperTest {

    @Autowired
    private UnterbrechungsUhrzeitModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val unterbrechungsUhrzeit = LocalDateTime.now();
            val entityToMap = new UnterbrechungsUhrzeit(wahlbezirkID, unterbrechungsUhrzeit);

            val result = unitUnderTest.toModel(entityToMap);

            val expecetedResult = new UnterbrechungsUhrzeitModel(wahlbezirkID, unterbrechungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val unterbrechungsUhrzeit = LocalDateTime.now();

            val modelToMap = new UnterbrechungsUhrzeitModel(wahlbezirkID, unterbrechungsUhrzeit);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new UnterbrechungsUhrzeit(wahlbezirkID, unterbrechungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
