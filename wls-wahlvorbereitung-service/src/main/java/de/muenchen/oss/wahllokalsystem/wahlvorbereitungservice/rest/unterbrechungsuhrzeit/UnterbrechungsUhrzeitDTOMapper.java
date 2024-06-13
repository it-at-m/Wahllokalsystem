package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface UnterbrechungsUhrzeitDTOMapper {

    UnterbrechungsUhrzeitDTO toDTO(UnterbrechungsUhrzeitModel model);

    UnterbrechungsUhrzeitModel toModel(String wahlbezirkID, UnterbrechungsUhrzeitWriteDTO dto);
}
