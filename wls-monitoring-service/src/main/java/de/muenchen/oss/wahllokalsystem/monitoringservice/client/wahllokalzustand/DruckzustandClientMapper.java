package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.common.TimeStampMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckzustandModel;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = { TimeStampMapper.class })
public interface DruckzustandClientMapper {

    @Mapping(target = "schnellmeldungSendenUhrzeit", source = "schnellmeldungSendenUhrzeit", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "schnellmeldungDruckUhrzeit", source = "schnellmeldungDruckUhrzeit", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "niederschriftSendenUhrzeit", source = "niederschriftSendenUhrzeit", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "niederschriftDruckUhrzeit", source = "niederschriftDruckUhrzeit", qualifiedByName = "localDateTimeToOffsetDateTime")
    DruckzustandDTO toDTO(DruckzustandModel druckzustandModel);
}
