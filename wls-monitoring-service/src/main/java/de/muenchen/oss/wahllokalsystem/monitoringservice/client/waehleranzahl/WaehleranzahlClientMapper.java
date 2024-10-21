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

    ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    @Mapping(target = "wahlID", source = "bezirkUndWahlID.wahlID")
    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlID.wahlbezirkID")
    @Mapping(target = "meldeZeitpunkt", source = "uhrzeit")
    WahlbeteiligungsMeldungDTO fromModelToRemoteClientDTO(WaehleranzahlModel waehleranzahlModel);

    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atOffset(DEFAULT_ZONE);
    }
}
