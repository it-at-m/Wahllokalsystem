package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorstandDTOMapper {

    WahlvorstandDTO toDTO(WahlvorstandModel model);

    WahlvorstandModel toModel(WahlvorstandDTO dto);
}
