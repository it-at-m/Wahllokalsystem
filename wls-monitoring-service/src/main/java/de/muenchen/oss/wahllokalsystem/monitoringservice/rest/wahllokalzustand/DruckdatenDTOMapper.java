package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckdatenModel;
import org.mapstruct.Mapper;

@Mapper
public interface DruckdatenDTOMapper {
    DruckdatenModel toDruckdatenModel(DruckdatenDTO dto);
}
