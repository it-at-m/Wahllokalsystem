package de.muenchen.oss.wahllokalsystem.monitoringservice.client.common;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface TimeStampMapper {

    ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return (null != localDateTime) ? localDateTime.atOffset(DEFAULT_ZONE) : null;
    }
}
