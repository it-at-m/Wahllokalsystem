package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;

import java.util.List;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten.WahlurneTestdatenfactory;
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
            List<Wahlurne> urnenanzahl = List.of(WahlurneTestdatenfactory.initValid("1234").build());
            val modelToMap = new BriefwahlvorbereitungModel(wahlbezirkID, urnenanzahl);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new BriefwahlvorbereitungDTO(wahlbezirkID, urnenanzahl);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            List<Wahlurne> urnenanzahl = List.of(WahlurneTestdatenfactory.initValid("1234").build());
            val dtoToMap = new BriefwahlvorbereitungWriteDTO(urnenanzahl);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new BriefwahlvorbereitungModel(wahlbezirkIDToMap, urnenanzahl);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
