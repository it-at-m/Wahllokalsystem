package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import java.time.Instant;
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
}
