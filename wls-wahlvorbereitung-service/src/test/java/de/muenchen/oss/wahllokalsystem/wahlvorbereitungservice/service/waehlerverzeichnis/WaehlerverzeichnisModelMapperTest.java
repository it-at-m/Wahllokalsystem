package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Waehlerverzeichnis;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WaehlerverzeichnisModelMapperTest {

    private final WaehlerverzeichnisModelMapper unitUnderTest = Mappers.getMapper(WaehlerverzeichnisModelMapper.class);

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;
            val modelToMap = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false, false, true,
                    true);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false, false, true,
                    true);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;
            val entityToMap = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false, false, true,
                    true);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedResult = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false, false,
                    true,
                    true);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
