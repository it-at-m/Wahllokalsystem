package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface FortsetzungsUhrzeitDTOMapper {

  FortsetzungsUhrzeitDTO toDTO(FortsetzungsUhrzeitModel model);

  FortsetzungsUhrzeitModel toModel(String wahlbezirkID, FortsetzungsUhrzeitWriteDTO dto);
}
