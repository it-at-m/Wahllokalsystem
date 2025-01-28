package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import java.time.Instant;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class AusdruckModelMapperTest {

    private final AusdruckModelMapper unitUnderTest = Mappers.getMapper(AusdruckModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnAusdruckReadModel_when_givenAusdruckEntity() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();
            val ausdruckEntity = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            val result = unitUnderTest.toModel(ausdruckEntity);

            val expectedResult = new AusdruckReadModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Meldungsart.class)
        void should_mapToEnumWithSameName_when_givenEntityMeldungsartEnumValue(final Meldungsart meldungsart) {
            val entityToMap = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(null, null, meldungsart), null, null);

            val result = unitUnderTest.toModel(entityToMap);

            Assertions.assertThat(result.wahlUndBezirkIDUndMeldungsart().getMeldungsart().name()).isEqualTo(meldungsart.name());
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toEntity(null, null)).isNull();
        }

        @Test
        void should_returnAusdruckEntity_when_givenAusdruckWriteModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();
            val ausdruckModel = new AusdruckWriteModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content);

            val result = unitUnderTest.toEntity(ausdruckModel, erstelltAm);

            val expectedResult = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Meldungsart.class)
        void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(final Meldungsart meldungsart) {
            val modelToMap = new AusdruckWriteModel(new WahlUndBezirkIDUndMeldungsart(null, null, meldungsart), null);

            val result = unitUnderTest.toEntity(modelToMap, Instant.now());

            Assertions.assertThat(result.getWahlUndBezirkIDUndMeldungsart().getMeldungsart().name()).isEqualTo(meldungsart.name());
        }
    }
}
