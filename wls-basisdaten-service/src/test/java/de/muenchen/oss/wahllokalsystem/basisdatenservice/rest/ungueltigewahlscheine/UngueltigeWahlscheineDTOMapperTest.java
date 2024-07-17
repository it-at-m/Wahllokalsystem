package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineWriteModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class UngueltigeWahlscheineDTOMapperTest {

    private final UngueltigeWahlscheineDTOMapper unitUnderTest = Mappers.getMapper(UngueltigeWahlscheineDTOMapper.class);

    @Nested
    class ToModel {

        @Nested
        class ToUngueltigeWahlscheineReferenceModel {

            @Test
            void isMapped() {
                val result = unitUnderTest.toModel("wahltagID", WahlbezirkArtDTO.BWB);

                val expectedResult = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.BWB);
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }

            @ParameterizedTest
            @EnumSource(WahlbezirkArtDTO.class)
            void alleEnumValuesAreMappable(final WahlbezirkArtDTO art) {
                val result = unitUnderTest.toModel(null, art);

                Assertions.assertThat(result.wahlbezirksart().toString()).isEqualTo(art.toString());
            }
        }

        @Nested
        class ToUngueltigeWahlscheineWriteModel {

            @Test
            void isMapped() {
                val referenceModel = new UngueltigeWahlscheineReferenceModel("wahltagID", WahlbezirkArtModel.UWB);
                val data = "data".getBytes();

                val result = unitUnderTest.toModel(referenceModel, data);

                val expectedResult = new UngueltigeWahlscheineWriteModel(referenceModel, data);
                Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
            }

        }
    }

}
