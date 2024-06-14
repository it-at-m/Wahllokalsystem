package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeit;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { EroeffnungsUhrzeitModelMapperImpl.class })
class EroeffnungsUhrzeitModelMapperTest {

    @Autowired
    private EroeffnungsUhrzeitModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val eroeffnungsUhrzeit = LocalDateTime.now();
            val entityToMap = new EroeffnungsUhrzeit(wahlbezirkID, eroeffnungsUhrzeit);

            val result = unitUnderTest.toModel(entityToMap);

            val expecetedResult = new EroeffnungsUhrzeitModel(wahlbezirkID, eroeffnungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val eroeffnungsUhrzeit = LocalDateTime.now();

            val modelToMap = new EroeffnungsUhrzeitModel(wahlbezirkID, eroeffnungsUhrzeit);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new EroeffnungsUhrzeit(wahlbezirkID, eroeffnungsUhrzeit);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
