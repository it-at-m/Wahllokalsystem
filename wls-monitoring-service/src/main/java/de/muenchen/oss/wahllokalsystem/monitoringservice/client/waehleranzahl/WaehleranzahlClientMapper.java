package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WaehleranzahlClientMapper {

    ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    @Mapping(target = "wahlID", ignore = true)
    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlID.wahlbezirkID")
    @Mapping(target = "meldeZeitpunkt", source = "uhrzeit")
    WahlbeteiligungsMeldungDTO fromModelToRemoteClientDTO(WaehleranzahlModel waehleranzahlModel);

    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTimeToOffsetDateTime(localDateTime, DEFAULT_ZONE_ID);
    }

    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        val zoneOffset = zoneId.getRules().getOffset(localDateTime);
        return localDateTime.atOffset(zoneOffset);
    }
}
