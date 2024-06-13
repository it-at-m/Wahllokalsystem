package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit.UnterbrechungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit.UnterbrechungsUhrzeitWriteDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface FortsetzungsUhrzeitDTOMapper {

    FortsetzungsUhrzeitDTO toDTO(FortsetzungsUhrzeitModel model);

    FortsetzungsUhrzeitModel toModel(String wahlbezirkID, FortsetzungsUhrzeitWriteDTO dto);
}
