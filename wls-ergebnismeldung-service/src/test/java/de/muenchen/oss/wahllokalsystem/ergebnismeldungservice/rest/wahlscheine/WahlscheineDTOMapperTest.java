package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlscheineDTOMapperTest {

    WahlscheineDTOMapper unitUnderTest = Mappers.getMapper(WahlscheineDTOMapper.class);

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
            val stimmabgabevermerke = 33L;
            val modelToMap = new WahlscheineModel(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID),stimmabgabevermerke);
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
            val stimmabgabevermerke = 33L;
            val dtoToMap = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

            val result = unitUnderTest.toModel(dtoToMap);

            val expectedResult = new WahlscheineModel(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
