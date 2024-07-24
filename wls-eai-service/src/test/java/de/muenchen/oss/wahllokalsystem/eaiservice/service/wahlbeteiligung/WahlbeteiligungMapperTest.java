package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.Wahlbeteiligung;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlbeteiligungMapperTest {

    private final WahlbeteiligungMapper unitUnderTest = Mappers.getMapper(WahlbeteiligungMapper.class);

    @Nested
    class ToDTO {

        @Test
        void ofWahlbeteiligung() {
            val id = UUID.randomUUID();
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val entityToMap = new Wahlbeteiligung(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);
            entityToMap.setId(id);

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void ofWahlbeteiligungsMeldungDTO() {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToMap = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val result = unitUnderTest.toEntity(dtoToMap);

            val expectedResult = new Wahlbeteiligung(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
