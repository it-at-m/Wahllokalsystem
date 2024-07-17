package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UngueltigeWahlscheineModelMapperTest {

    private final UngueltigeWahlscheineModelMapper unitUnderTest = Mappers.getMapper(UngueltigeWahlscheineModelMapper.class);

    @Nested
    class ToID {

        @Test
        void isMapped() {
            val wahltagID = "wahltagID";

            val modelToMap = new UngueltigeWahlscheineReferenceModel(wahltagID, WahlbezirkArtModel.UWB);

            val result = unitUnderTest.toID(modelToMap);

            val expectedResult = new WahltagIdUndWahlbezirksart(wahltagID, WahlbezirkArt.UWB);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val wahltagID = "wahltagID";
            val data = "the test data".getBytes();

            val modelToMap = new UngueltigeWahlscheineWriteModel(new UngueltigeWahlscheineReferenceModel(wahltagID, WahlbezirkArtModel.BWB), data);

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new UngueltigeWahlscheine(new WahltagIdUndWahlbezirksart(wahltagID, WahlbezirkArt.BWB), data);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
