package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.Waehleranzahl;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WaehleranzahlModelMapperTest {

    private final WaehleranzahlModelMapper unitUnderTest = Mappers.getMapper(WaehleranzahlModelMapper.class);

    @Test
    void should_returnEqualWaehleranzahlModel_when_WaehleranzahlIsMapped() {
        val wahlID = "wahlID01";
        val wahlbezirkID = "wahlbezirkID01";
        BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        val anzahlWaehler = 99L;
        LocalDateTime uhrzeit = LocalDateTime.now();
        val waehleranzahlEntity = new Waehleranzahl(bezirkUndWahlID, anzahlWaehler, uhrzeit);

        val waehleranzahlModel = unitUnderTest.toModel(waehleranzahlEntity);

        val expectedResult = new WaehleranzahlModel(bezirkUndWahlID, anzahlWaehler, uhrzeit);

        Assertions.assertThat(waehleranzahlModel).isEqualTo(expectedResult);
    }

    @Test
    void should_returnEqualWaehleranzahl_when_WaehleranzahlModelIsMapped() {
        val wahlID = "wahlID01";
        val wahlbezirkID = "wahlbezirkID01";
        BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        val anzahlWaehler = 99L;
        LocalDateTime uhrzeit = LocalDateTime.now();
        val waehleranzahlModel = new WaehleranzahlModel(bezirkUndWahlID, anzahlWaehler, uhrzeit);

        val waehleranzahlEntity = unitUnderTest.toEntity(waehleranzahlModel);

        val expectedResult = new Waehleranzahl(bezirkUndWahlID, anzahlWaehler, uhrzeit);

        Assertions.assertThat(waehleranzahlEntity).isEqualTo(expectedResult);
    }
}
