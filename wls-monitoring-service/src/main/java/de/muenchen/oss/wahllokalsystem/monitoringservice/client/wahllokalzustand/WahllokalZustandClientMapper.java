package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = { DruckzustandClientMapper.class })
public interface WahllokalZustandClientMapper {

    ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    @Mapping(target = "letzteAbmeldung", source = "letzteAbmeldung", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "zuletztGesehen", source = "zuletztGesehen", qualifiedByName = "localDateTimeToOffsetDateTime")
    WahllokalZustandDTO toDTO(WahllokalZustandModel wahllokalZustandModel);

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atOffset(DEFAULT_ZONE);
    }
}
