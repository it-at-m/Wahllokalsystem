package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
        void should_return_RemoteClientDTO_when_modelIsGiven() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val anzahlWahler = 99L;
            val meldeZeitpunkt = LocalDateTime.now();
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);

            val waehleranzahlModel = new WaehleranzahlModel(bezirkUndWahlID, anzahlWahler, meldeZeitpunkt);
            Assertions.assertThat(waehleranzahlModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromModelToRemoteClientDTO(waehleranzahlModel);

            val zoneOffset = ZoneId.systemDefault().getRules().getOffset(meldeZeitpunkt);
            val expectedWahlbeteiligungsMeldungDTO = new WahlbeteiligungsMeldungDTO().wahlID(null).wahlbezirkID(wahlbezirkID).anzahlWaehler(anzahlWahler)
                    .meldeZeitpunkt(meldeZeitpunkt.atOffset(zoneOffset));
            Assertions.assertThat(result).isEqualTo(expectedWahlbeteiligungsMeldungDTO);
        }
    }

    @Nested
    class LocalDateTimeToOffsetDateTime {

        @Test
        void should_useSummerSeasonOffset_when_mapping() {
            val localDateTime = LocalDateTime.parse("2024-06-12T12:13:14.567");
            val zoneID = ZoneId.of("Europe/Berlin");

            val result = unitUnderTest.localDateTimeToOffsetDateTime(localDateTime, zoneID);

            val expectedResult = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(2));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_useWinterSeasonOffset_when_mapping() {
            val localDateTime = LocalDateTime.parse("2024-11-12T12:13:14.567");
            val zoneID = ZoneId.of("Europe/Berlin");
            val result = unitUnderTest.localDateTimeToOffsetDateTime(localDateTime, zoneID);

            val expectedResult = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(1));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
