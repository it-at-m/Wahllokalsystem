package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlscheineDTOMapper {

    WahlscheineDTO toDTO(WahlscheineModel wahlscheineModel);

    WahlscheineModel toModel(WahlscheineDTO wahlscheineDTO);
}
