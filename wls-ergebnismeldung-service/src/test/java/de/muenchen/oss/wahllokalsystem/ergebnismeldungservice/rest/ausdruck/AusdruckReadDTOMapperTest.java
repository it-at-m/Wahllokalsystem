package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import java.time.Instant;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class AusdruckReadDTOMapperTest {

    AusdruckReadDTOMapper unitUnderTest = Mappers.getMapper(AusdruckReadDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnDTO_when_givenModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();

            val modelToMap = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new AusdruckReadDTO(wahlbezirkID, wahlID, meldungsart, content, erstelltAm);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Meldungsart.class)
        void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(final Meldungsart meldungsart) {
            val modelToMap = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(null, null, meldungsart), null, null);

            val result = unitUnderTest.toDTO(modelToMap);

            Assertions.assertThat(result.meldungsart().name()).isEqualTo(meldungsart.name());
        }
    }

    @Nested
    class ToDTOList {
        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.fromListOfAusdruckModelToListOfAusdruckReadDTO(null)).isNull();
        }

        @Test
        void should_returnListOfAusdruckReadDTO_when_listOfAusdruckModelIsGiven() {
            val erstelltAm = Instant.now();
            val modelsInput = createListOfAusdruckModel(erstelltAm);
            val dtosExpected = createListOfAusdruckReadDTO(erstelltAm);

            val result = unitUnderTest.fromListOfAusdruckModelToListOfAusdruckReadDTO(modelsInput);
            Assertions.assertThat(result).isEqualTo(dtosExpected);
        }
    }

    private List<AusdruckReadDTO> createListOfAusdruckReadDTO(Instant erstelltAm) {
        val wahlID = "wahlID";
        val content = "Testausdruck";

        val ausdruckReadDTO1 = new AusdruckReadDTO("wahlbezirkID01", wahlID, Meldungsart.V1, content, erstelltAm);
        val ausdruckReadDTO2 = new AusdruckReadDTO("wahlbezirkID02", wahlID, Meldungsart.V1, content, erstelltAm);
        val ausdruckReadDTO3 = new AusdruckReadDTO("wahlbezirkID03", wahlID, Meldungsart.V3, content, erstelltAm);

        return List.of(ausdruckReadDTO1, ausdruckReadDTO2, ausdruckReadDTO3);

    }

    private List<AusdruckModel> createListOfAusdruckModel(Instant erstelltAm) {
        val wahlID = "wahlID";
        val content = "Testausdruck";

        val ausdruckModel1 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID01", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckModel2 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID02", wahlID, Meldungsart.V1), content, erstelltAm);
        val ausdruckModel3 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID03", wahlID, Meldungsart.V3), content, erstelltAm);

        return List.of(ausdruckModel1, ausdruckModel2, ausdruckModel3);
    }
}
