package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahllokalZustandDTOMapper {
    WahllokalZustandModel toModel(final WahllokalZustandDTO dto);
}
