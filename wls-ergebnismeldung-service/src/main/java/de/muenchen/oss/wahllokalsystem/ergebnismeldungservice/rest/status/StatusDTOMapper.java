package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import org.mapstruct.Mapper;

@Mapper
public interface StatusDTOMapper {

    StatusDTO toDTO(StatusModel statusModel);

    StatusModel toModel(StatusDTO statusDTO);
}
