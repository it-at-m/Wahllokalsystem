package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.SendungsdatenModel;
import org.mapstruct.Mapper;

@Mapper
public interface SendungsdatenDTOMapper {
    SendungsdatenModel toSendungsdatenModel(SendungsdatenDTO dto);
}
