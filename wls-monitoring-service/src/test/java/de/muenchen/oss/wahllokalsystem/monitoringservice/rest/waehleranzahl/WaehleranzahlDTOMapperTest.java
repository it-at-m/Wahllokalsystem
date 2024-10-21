package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WaehleranzahlDTOMapperTest {

    private final WaehleranzahlDTOMapper unitUnderTest = Mappers.getMapper(WaehleranzahlDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_returnNull_when_ModelIsNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnNull_when_DTOIsNull() {
            Assertions.assertThat(unitUnderTest.toSetModel(null, null)).isNull();
        }

        @Test
        void should_mapModelToDto_when_ModelIsNotNull() {
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            long anzahlWaehler = 99L;
            LocalDateTime uhrzeit = LocalDateTime.now();
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val modelInput = new WaehleranzahlModel(bezirkUndWahlID, anzahlWaehler, uhrzeit);
            val dtoExpected = new WaehleranzahlDTO(wahlID, wahlbezirkID, anzahlWaehler, uhrzeit);

            val result = unitUnderTest.toDTO(modelInput);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }

        @Test
        void should_mapDtoToModel_when_DtoIsNotNull() {
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            long anzahlWaehler = 99L;
            LocalDateTime uhrzeit = LocalDateTime.now();
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val dtoInput = new WaehleranzahlDTO(wahlID, wahlbezirkID, anzahlWaehler, uhrzeit);
            val modelExpected = new WaehleranzahlModel(bezirkUndWahlID, anzahlWaehler, uhrzeit);

            val result = unitUnderTest.toSetModel(bezirkUndWahlID, dtoInput);
            Assertions.assertThat(result).isEqualTo(modelExpected);
        }
    }
}
