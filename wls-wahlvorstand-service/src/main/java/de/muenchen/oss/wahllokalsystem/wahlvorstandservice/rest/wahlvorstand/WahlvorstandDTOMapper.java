package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorstandDTOMapper {

    WahlvorstandDTO toDTO(WahlvorstandModel model);

    WahlvorstandModel toModel(String wahlbezirkID, WahlvorstandDTO dto);
}
