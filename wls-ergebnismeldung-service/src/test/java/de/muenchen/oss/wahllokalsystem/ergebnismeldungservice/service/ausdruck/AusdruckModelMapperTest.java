package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import java.time.Instant;
import java.util.List;
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
        void should_returnAusdruckModel_when_givenAusdruckEntity() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();
            val ausdruckEntity = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            val result = unitUnderTest.toModel(ausdruckEntity);

            val expectedResult = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

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
            Assertions.assertThat(unitUnderTest.toEntity(null)).isNull();
        }

        @Test
        void should_returnAusdruckEntity_when_givenAusdruckModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();
            val ausdruckModel = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            val result = unitUnderTest.toEntity(ausdruckModel);

            val expectedResult = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Meldungsart.class)
        void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(final Meldungsart meldungsart) {
            val modelToMap = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(null, null, meldungsart), null, null);

            val result = unitUnderTest.toEntity(modelToMap);

            Assertions.assertThat(result.getWahlUndBezirkIDUndMeldungsart().getMeldungsart().name()).isEqualTo(meldungsart.name());
        }
    }

    @Nested
    class ToModelist {
        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModelList(null)).isNull();
        }

        @Test
        void should_returnListOfAusdruckModel_when_listOfAusdruckIsGiven() {
            val erstelltAm = Instant.now();
            val entitiesInput = createListOfAusdruckEntities(erstelltAm);
            val modelsExpected = createListOfAusdruckModels(erstelltAm);

            val result = unitUnderTest.toModelList(entitiesInput);
            Assertions.assertThat(result).isEqualTo(modelsExpected);
        }
    }

    private List<AusdruckModel> createListOfAusdruckModels(final Instant erstelltAm) {
        val wahlID = "wahlID";
        val content = "Testausdruck";

        val ausdruckModel1 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID01", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckModel2 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID02", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckModel3 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID03", wahlID, Meldungsart.V3), content, erstelltAm);

        return List.of(ausdruckModel1, ausdruckModel2, ausdruckModel3);

    }

    private List<Ausdruck> createListOfAusdruckEntities(Instant erstelltAm) {
        val wahlID = "wahlID";
        val content = "Testausdruck";

        val ausdruckEntity1 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID01", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckEntity2 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID02", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckEntity3 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID03", wahlID, Meldungsart.V3), content, erstelltAm);

        return List.of(ausdruckEntity1, ausdruckEntity2, ausdruckEntity3);
    }
}
