package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicherStimmzettelModel;
import java.util.Collection;
import org.mapstruct.Mapper;

@Mapper
public interface BedenklicherStimmzettelDTOMapper {

  Collection<BedenklicherStimmzettelDTO> toDTO(
      Collection<BedenklicherStimmzettelModel> collectionOfModels);

  BedenklicherStimmzettelDTO toDTO(BedenklicherStimmzettelModel model);

  BedenklicherStimmzettelModel toModel(BedenklicherStimmzettelDTO dto);
}
