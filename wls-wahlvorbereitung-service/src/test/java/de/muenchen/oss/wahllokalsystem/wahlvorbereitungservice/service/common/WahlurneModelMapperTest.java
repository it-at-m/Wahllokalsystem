package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlurneModelMapperTest {

    private final WahlurneModelMapper unitUnderTest = Mappers.getMapper(WahlurneModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val wahlID = "wahlID";
            val anzahl = 23;
            val urneVersiegelt = true;
            val entityToMap = new Wahlurne(wahlID, anzahl, urneVersiegelt);

            val result = unitUnderTest.toModel(entityToMap);

            val expectedResult = new WahlurneModel(wahlID, anzahl, urneVersiegelt);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
