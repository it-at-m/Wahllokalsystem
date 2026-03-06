package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel.StimmzettelModel;
import org.mapstruct.Mapper;

@Mapper
public interface StimmzettelDTOMapper {
  StimmzettelModel toModel(WaehlerStimmzettelDTO dto);

  WaehlerStimmzettelDTO toDTO(StimmzettelModel model);
}
