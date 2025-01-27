package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class AusdruckWriteDTOMapperTest {

    AusdruckWriteDTOMapper unitUnderTest = Mappers.getMapper(AusdruckWriteDTOMapper.class);

    @Nested
    class ToModel {
        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null, null)).isNull();
        }

        @Test
        void should_returnModel_when_givenDTO() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";

            val dtoToMap = new AusdruckWriteDTO(content);

            val result = unitUnderTest.toModel(dtoToMap, new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart));

            val expectedResult = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, null);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Meldungsart.class)
        void should_mapToEnumWithSameName_when_givenModelMeldungsartEnumValue(final Meldungsart meldungsart) {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val content = "Testausdruck";

            val dtoToMap = new AusdruckWriteDTO(content);

            val result = unitUnderTest.toModel(dtoToMap, new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart));

            Assertions.assertThat(result.wahlUndBezirkIDUndMeldungsart().getMeldungsart().name()).isEqualTo(meldungsart.name());
        }
    }
}
