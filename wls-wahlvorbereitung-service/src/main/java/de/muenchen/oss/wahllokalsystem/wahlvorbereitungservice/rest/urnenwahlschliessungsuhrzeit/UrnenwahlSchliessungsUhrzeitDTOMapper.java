package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface UrnenwahlSchliessungsUhrzeitDTOMapper {

    UrnenwahlSchliessungsUhrzeitDTO toDTO(UrnenwahlSchliessungsUhrzeitModel model);

    UrnenwahlSchliessungsUhrzeitModel toModel(String wahlbezirkID, UrnenwahlSchliessungsUhrzeitWriteDTO dto);
}
