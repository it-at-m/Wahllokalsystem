package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Briefwahlvorbereitung;

import java.util.List;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten.WahlurneTestdatenfactory;
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
            List<Wahlurne> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValid("1234").build());
            val entityToMap = new Briefwahlvorbereitung(wahlbezirkID, urnenanzahl1);

            val result = unitUnderTest.toModel(entityToMap);

            val expecetedResult = new BriefwahlvorbereitungModel(wahlbezirkID, List.of(new WahlurneModel("1234", 1, true)));

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            List<WahlurneModel> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidModel("1234").build());
            val modelToMap = new BriefwahlvorbereitungModel(wahlbezirkID, urnenanzahl1);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new Briefwahlvorbereitung(wahlbezirkID, List.of(new Wahlurne("1234", 0, null)));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
