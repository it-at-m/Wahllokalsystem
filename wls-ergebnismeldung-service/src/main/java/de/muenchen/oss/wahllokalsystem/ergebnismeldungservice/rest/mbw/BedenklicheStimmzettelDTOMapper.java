package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicheStimmzettelModel;
import org.mapstruct.Mapper;

@Mapper
public interface BedenklicheStimmzettelDTOMapper {

  BedenklicheStimmzettelDTO toDTO(BedenklicheStimmzettelModel model);

  BedenklicheStimmzettelModel toWriteModel(BedenklicheStimmzettelDTO dto);
}
