package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlDTOMapper {

    WahlDTO toDTO(WahlModel wahlModel);

    WahlModel toModel(WahlDTO wahlDTO);
}
