package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlscheineModelMapperTest {

    private WahlscheineModelMapper unitUnderTest = Mappers.getMapper(WahlscheineModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnWahlscheineModel_when_givenWahlscheineEntity() {
            val wahlscheineEntiy = new Wahlscheine(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 33);

            val result = unitUnderTest.toModel(wahlscheineEntiy);

            val expectedResult = new WahlscheineModel(
                    new BezirkUndWahlID("wahlID", "wahlbezirkID"), 33L);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toEntity(null)).isNull();
        }

        @Test
        void should_returnWahlscheineEntity_when_givenWahlscheineModel() {
            val wahlscheineModel = new WahlscheineModel(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 33L);

            val result = unitUnderTest.toEntity(wahlscheineModel);

            val expectedResult = new Wahlscheine(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 33);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
