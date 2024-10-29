package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.common.TimeStampMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { DruckzustandClientMapper.class, TimeStampMapper.class })
public interface WahllokalZustandClientMapper {

    @Mapping(target = "letzteAbmeldung", source = "letzteAbmeldung", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "zuletztGesehen", source = "zuletztGesehen", qualifiedByName = "localDateTimeToOffsetDateTime")
    WahllokalZustandDTO toDTO(WahllokalZustandModel wahllokalZustandModel);
}
