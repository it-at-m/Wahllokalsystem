package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WaehlerverzeichnisDTOMapperTest {

    private final WaehlerverzeichnisDTOMapper unitUnderTest = Mappers.getMapper(WaehlerverzeichnisDTOMapper.class);

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;

            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);
            val dtoToMap = new WaehlerverzeichnisWriteDTO(true, true, false, false);

            val result = unitUnderTest.toModel(waehlerverzeichnisReference, dtoToMap);

            val expectedResult = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), true, true, false,
                    false);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void referenceIsNull() {
            val dtoToMap = new WaehlerverzeichnisWriteDTO(true, true, false, false);

            val result = unitUnderTest.toModel(null, dtoToMap);

            val expectedResult = new WaehlerverzeichnisModel(null, true, true, false,
                    false);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void dtoIsNull() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;

            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);

            val result = unitUnderTest.toModel(waehlerverzeichnisReference, null);

            val expectedResult = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), null, null, null,
                    null);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToDto {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;
            val modelToMap = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), true, true, false,
                    false);

            val result = unitUnderTest.toDto(modelToMap);

            val expectedResult = new WaehlerverzeichnisDTO(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), true, true, false,
                    false);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
