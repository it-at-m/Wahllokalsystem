package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import java.util.Collections;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { BriefwahlvorbereitungDTOMapperImpl.class })
class BriefwahlvorbereitungDTOMapperTest {

    @Autowired
    private BriefwahlvorbereitungDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val modelToMap = new BriefwahlvorbereitungModel(wahlbezirkID, Collections.emptyList());

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new BriefwahlvorbereitungDTO(wahlbezirkID, Collections.emptyList());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val dtoToMap = new BriefwahlvorbereitungWriteDTO(Collections.emptyList());
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new BriefwahlvorbereitungModel(wahlbezirkIDToMap, Collections.emptyList());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
