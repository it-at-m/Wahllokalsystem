package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface EroeffnungsUhrzeitDTOMapper {

    EroeffnungsUhrzeitDTO toDTO(EroeffnungsUhrzeitModel model);

    EroeffnungsUhrzeitModel toModel(String wahlbezirkID, EroeffnungsUhrzeitWriteDTO dto);
}
