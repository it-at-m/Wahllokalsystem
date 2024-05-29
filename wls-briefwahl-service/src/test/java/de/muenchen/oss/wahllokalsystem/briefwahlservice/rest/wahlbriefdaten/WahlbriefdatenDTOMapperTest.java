package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenModel;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbriefdatenDTOMapperTest {

    private final WahlbriefdatenDTOMapper unitUnderTest = Mappers.getMapper(WahlbriefdatenDTOMapper.class);

    @Nested
    class ToDTO {

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

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new WahlbriefdatenDTO(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

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
            val dtoForMapping = new WahlbriefdatenWriteDTO(wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);

            val result = unitUnderTest.toModel(wahlbezirkID, dtoForMapping);

            val expectedResult = new WahlbriefdatenModel(wahlbezirkID, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void mappingWithoutWahlbezirkID() {
            val wahlbriefe = 12L;
            val verzeichnisseUngueltig = 3L;
            val nachtraege = 23L;
            val nachtraeglichUeberbrachte = 35L;
            val zeitNachtraeglichUeberbrachte = LocalDateTime.now();
            val dtoForMapping = new WahlbriefdatenWriteDTO(wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);

            val result = unitUnderTest.toModel(null, dtoForMapping);

            val expectedResult = new WahlbriefdatenModel(null, wahlbriefe, verzeichnisseUngueltig, nachtraege, nachtraeglichUeberbrachte,
                    zeitNachtraeglichUeberbrachte);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void mappingWithoutDTO() {
            val wahlbezirkID = "wahlbezirkID";

            val result = unitUnderTest.toModel(wahlbezirkID, null);

            val expectedResult = new WahlbriefdatenModel(wahlbezirkID, null, null, null, null, null);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
