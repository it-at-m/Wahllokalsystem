package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckzustandModel;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface DruckzustandClientMapper {

    ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    @Mapping(target = "schnellmeldungSendenUhrzeit", source = "schnellmeldungSendenUhrzeit", qualifiedByName = "druckzustandLocalDateTimeToOffsetDateTime")
    @Mapping(target = "schnellmeldungDruckUhrzeit", source = "schnellmeldungDruckUhrzeit", qualifiedByName = "druckzustandLocalDateTimeToOffsetDateTime")
    @Mapping(target = "niederschriftSendenUhrzeit", source = "niederschriftSendenUhrzeit", qualifiedByName = "druckzustandLocalDateTimeToOffsetDateTime")
    @Mapping(target = "niederschriftDruckUhrzeit", source = "niederschriftDruckUhrzeit", qualifiedByName = "druckzustandLocalDateTimeToOffsetDateTime")
    DruckzustandDTO toDTO(DruckzustandModel druckzustandModel);

    @Named("druckzustandLocalDateTimeToOffsetDateTime")
    default OffsetDateTime druckzustandLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atOffset(DEFAULT_ZONE);
    }
}
