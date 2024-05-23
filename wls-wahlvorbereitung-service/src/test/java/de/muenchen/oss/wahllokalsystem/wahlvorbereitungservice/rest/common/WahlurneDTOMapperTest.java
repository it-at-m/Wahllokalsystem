package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlurneDTOMapperTest {

    private final WahlurneDTOMapper unitUnderTest = Mappers.getMapper(WahlurneDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void isMapped() {
            val wahlID = "wahlID";
            val anzahl = 4711;
            val urneVersiegelt = true;
            val modelToMap = new WahlurneModel(wahlID, anzahl, urneVersiegelt);

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new WahlurneDTO(wahlID, anzahl, urneVersiegelt);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
