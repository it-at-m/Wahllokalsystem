package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlvorstandModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorstandDTOMapper {

    WahlvorstandDTO toDTO(WahlvorstandModel wahlvorstandModel);

    WahlvorstandModel toModel(WahlvorstandDTO wahlvorstandDTO);
}
