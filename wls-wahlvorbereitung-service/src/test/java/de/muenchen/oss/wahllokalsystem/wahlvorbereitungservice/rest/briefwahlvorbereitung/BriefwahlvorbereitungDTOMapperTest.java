package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import java.util.List;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
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
            List<WahlurneModel> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidModel("1234").build());
            val modelToMap = new BriefwahlvorbereitungModel(wahlbezirkID, urnenanzahl1);

            val result = unitUnderTest.toDTO(modelToMap);
            val expectedResult = new BriefwahlvorbereitungDTO(wahlbezirkID, List.of(new WahlurneDTO("1234", 0, null)));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            List<WahlurneDTO> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidDTO("1234").build());
            val dtoToMap = new BriefwahlvorbereitungWriteDTO(urnenanzahl1);
            val wahlbezirkIDToMap = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkIDToMap, dtoToMap);

            val expectedResult = new BriefwahlvorbereitungModel(wahlbezirkIDToMap, List.of(new WahlurneModel("1234", 0, null)));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
