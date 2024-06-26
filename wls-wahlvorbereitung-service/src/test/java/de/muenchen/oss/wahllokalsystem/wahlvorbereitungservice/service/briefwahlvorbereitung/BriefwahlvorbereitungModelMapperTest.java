package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Briefwahlvorbereitung;

import java.util.Collections;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { BriefwahlvorbereitungModelMapperImpl.class })
class BriefwahlvorbereitungModelMapperTest {

    @Autowired
    private BriefwahlvorbereitungModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val entityToMap = new Briefwahlvorbereitung(wahlbezirkID, Collections.emptyList());

            val result = unitUnderTest.toModel(entityToMap);

            val expecetedResult = new BriefwahlvorbereitungModel(wahlbezirkID, Collections.emptyList());

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";

            val modelToMap = new BriefwahlvorbereitungModel(wahlbezirkID, Collections.emptyList());

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new Briefwahlvorbereitung(wahlbezirkID, Collections.emptyList());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
