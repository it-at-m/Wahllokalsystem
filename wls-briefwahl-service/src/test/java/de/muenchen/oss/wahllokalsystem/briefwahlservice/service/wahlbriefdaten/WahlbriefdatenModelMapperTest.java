package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.Wahlbriefdaten;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbriefdatenModelMapperTest {

    private final WahlbriefdatenModelMapper unitUnderTest = Mappers.getMapper(WahlbriefdatenModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlbriefe = 12L;
            val verzeichnisseUngueltig = 3L;
            val nachtraege = 23L;
            val nachtraeglichUeberbrachte = 35L;
            val zeitNachtraeglichUeberbrachte = LocalDateTime.now();
            val entityToMap = new Wahlbriefdaten(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedResult = new WahlbriefdatenModel(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlbriefe = 12L;
            val verzeichnisseUngueltig = 3L;
            val nachtraege = 23L;
            val nachtraeglichUeberbrachte = 35L;
            val zeitNachtraeglichUeberbrachte = LocalDateTime.now();
            val modelToMap = new WahlbriefdatenModel(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new Wahlbriefdaten(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

}
