package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class WaehleranzahlClientMapperTest {

    private final WaehleranzahlClientMapper unitUnderTest = Mappers.getMapper(WaehleranzahlClientMapper.class);

    @Nested
    class FromModelToRemoteClientDTO {

        @Test
        void isMappedWithSomething() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val anzahlWahler = 99L;
            val meldeZeitpunkt = LocalDateTime.now();
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val waehleranzahlModel = new WaehleranzahlModel(bezirkUndWahlID, anzahlWahler, meldeZeitpunkt);
            Assertions.assertThat(waehleranzahlModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromModelToRemoteClientDTO(waehleranzahlModel);

            val expectedWahlbeteiligungsMeldungDTO = new WahlbeteiligungsMeldungDTO().wahlID(wahlID).wahlbezirkID(wahlbezirkID).anzahlWaehler(anzahlWahler)
                    .meldeZeitpunkt(meldeZeitpunkt.atOffset(ZoneOffset.UTC));
            Assertions.assertThat(result).isEqualTo(expectedWahlbeteiligungsMeldungDTO);
        }
    }
}
