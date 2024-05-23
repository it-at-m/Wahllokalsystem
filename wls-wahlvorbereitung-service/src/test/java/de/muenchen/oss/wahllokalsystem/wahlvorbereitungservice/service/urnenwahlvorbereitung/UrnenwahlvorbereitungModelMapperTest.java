package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModelMapperImpl;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { WahlurneModelMapperImpl.class, UrnenwahlvorbereitungModelMapperImpl.class })
class UrnenwahlvorbereitungModelMapperTest {

    @Autowired
    private UrnenwahlvorbereitungModelMapper unitUnderTest;

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val anzahlKabinen = 21;
            val anzahlNebenraeume = 3;
            val anzahlTische = 7;
            val urnen = List.of(new Wahlurne("wahlID1", 3, true), new Wahlurne("wahlID5", 17, false));
            val entityToMap = new UrnenwahlVorbereitung(wahlbezirkID, urnen, anzahlKabinen, anzahlTische, anzahlNebenraeume);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedUrnen = List.of(new WahlurneModel("wahlID1", 3, true), new WahlurneModel("wahlID5", 17, false));
            val expecetedResult = new UrnenwahlvorbereitungModel(wahlbezirkID, anzahlKabinen, anzahlTische, anzahlNebenraeume, expectedUrnen);

            Assertions.assertThat(result).isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val anzahlKabinen = 21;
            val anzahlNebenraeume = 3;
            val anzahlTische = 7;
            val urnen = List.of(new WahlurneModel("wahlID1", 3, true), new WahlurneModel("wahlID5", 17, false));
            val modelToMap = new UrnenwahlvorbereitungModel(wahlbezirkID, anzahlKabinen, anzahlTische, anzahlNebenraeume, urnen);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedUrnen = List.of(new Wahlurne("wahlID1", 3, true), new Wahlurne("wahlID5", 17, false));
            val expectedResult = new UrnenwahlVorbereitung(wahlbezirkID, expectedUrnen, anzahlKabinen, anzahlTische, anzahlNebenraeume);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
