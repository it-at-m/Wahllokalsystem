package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTOMapperImpl;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungModel;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { UrnenwahlvorbereitungDTOMapperImpl.class, WahlurneDTOMapperImpl.class })
class UrnenwahlvorbereitungDTOMapperTest {

    @Autowired
    private UrnenwahlvorbereitungDTOMapper unitUnderTest;

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val anzahlKabinen = 12;
            val anzahlWahltische = 21;
            val anzahlNebenraeume = 4;
            val urne1 = new WahlurneModel("wahlID1", 2, true);
            val urne2 = new WahlurneModel("wahlID2", 4, false);
            val urnen = List.of(urne1, urne2);
            val modelToMap = new UrnenwahlvorbereitungModel(wahlbezirkID, anzahlKabinen, anzahlWahltische, anzahlNebenraeume, urnen);

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedUrnen = List.of(new WahlurneDTO("wahlID1", 2, true), new WahlurneDTO("wahlID2", 4, false));
            val expectedResult = new UrnenwahlvorbereitungDTO(wahlbezirkID, anzahlKabinen, anzahlWahltische, anzahlNebenraeume, expectedUrnen);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
