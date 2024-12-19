package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StimmzettelumschlaegeModelMapperTest {

    private final StimmzettelumschlaegeModelMapper unitUnderTest = Mappers.getMapper(StimmzettelumschlaegeModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnStimmzettelumschlaegeModel_when_givenStimmzettelumschlaegeEntity() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11;
            val wahlscheineEntity = new Stimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    (long) anzahlWaehler2);

            val result = unitUnderTest.toModel(wahlscheineEntity);
            val expectedResult = new StimmzettelumschlaegeModel(
                    new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

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
        void should_returnStimmzettelumschlaegeEntity_when_givenStimmzettelumschlaegeModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11;
            val wahlscheineModel = new StimmzettelumschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    anzahlWaehler2);

            val result = unitUnderTest.toEntity(wahlscheineModel);
            val expectedResult = new Stimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    (long) anzahlWaehler2);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
