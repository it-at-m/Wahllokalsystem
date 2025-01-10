package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StimmzettelumschlaegeDTOMapperTest {

    StimmzettelumschlaegeDTOMapper unitUnderTest = Mappers.getMapper(StimmzettelumschlaegeDTOMapper.class);

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
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11;
            val modelToMap = new StimmzettelumschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    (long) anzahlWaehler2);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnModel_when_givenDTO() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11;
            val dtoToMap = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    (long) anzahlWaehler2);

            val result = unitUnderTest.toModel(dtoToMap);

            val expectedResult = new StimmzettelumschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler,
                    anzahlWaehler2);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
