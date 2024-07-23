package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class HandbuchModelMapperTest {

    private final HandbuchModelMapper unitUnderTest = Mappers.getMapper(HandbuchModelMapper.class);

    @Nested
    class ToEntityID {

        @Test
        void isMapped() {
            val modelToMap = new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.UWB);

            val result = unitUnderTest.toEntityID(modelToMap);

            val expectedResult = new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(WahlbezirkArtModel.class)
        void allWahlbezirksArtEnumValuesAreMapped(final WahlbezirkArtModel wahlbezirkArtModel) {
            Assertions.assertThat(unitUnderTest.toEntityID(new HandbuchReferenceModel("", wahlbezirkArtModel)).getWahlbezirksart().toString())
                    .isEqualTo(wahlbezirkArtModel.toString());
        }
    }

    @Nested
    class ToEntity {

        @Test
        void isMapped() {
            val modelToMap = new HandbuchWriteModel(new HandbuchReferenceModel("wahltagID", WahlbezirkArtModel.BWB), "helloWorld".getBytes());

            val result = unitUnderTest.toEntity(modelToMap);

            val expectedResult = new Handbuch(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.BWB), "helloWorld".getBytes());
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(WahlbezirkArtModel.class)
        void allWahlbezirksArtEnumValuesAreMapped(final WahlbezirkArtModel wahlbezirkArtModel) {
            val modelToMap = new HandbuchWriteModel(new HandbuchReferenceModel("", wahlbezirkArtModel), "".getBytes());

            val result = unitUnderTest.toEntity(modelToMap);

            Assertions.assertThat(result.getWahltagIdUndWahlbezirksart().getWahlbezirksart().toString()).isEqualTo(wahlbezirkArtModel.toString());
        }
    }

}
