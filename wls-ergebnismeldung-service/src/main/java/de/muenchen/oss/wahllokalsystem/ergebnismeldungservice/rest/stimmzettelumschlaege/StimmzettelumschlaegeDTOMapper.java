package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModel;
import org.mapstruct.Mapper;

@Mapper
public interface StimmzettelumschlaegeDTOMapper {

    StimmzettelumschlaegeDTO toDTO(StimmzettelumschlaegeModel stimmzettelumschlaegeModel);

    StimmzettelumschlaegeModel toModel(StimmzettelumschlaegeDTO stimmzettelumschlaegeDTO);
}
