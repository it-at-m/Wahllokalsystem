package de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.common.TimeStampMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { TimeStampMapper.class })
public interface WaehleranzahlClientMapper {

    @Mapping(target = "wahlID", source = "bezirkUndWahlID.wahlID")
    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlID.wahlbezirkID")
    @Mapping(target = "meldeZeitpunkt", source = "uhrzeit", qualifiedByName = "localDateTimeToOffsetDateTime")
    WahlbeteiligungsMeldungDTO toDTO(WaehleranzahlModel waehleranzahlModel);
}
