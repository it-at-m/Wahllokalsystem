package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlbriefdatenDTOMapper {

    WahlbriefdatenDTO toDTO(WahlbriefdatenModel model);

    WahlbriefdatenModel toModel(String wahlbezirkID, WahlbriefdatenWriteDTO dto);
}
