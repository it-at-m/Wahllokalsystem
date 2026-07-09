package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.StimmzettelOfTeamModel;
import org.mapstruct.Mapper;

@Mapper
public interface StimmzettelDTOMapper {

  StimmzettelOfTeamModel toModel(StimmzettelOfTeamDTO stimmzettelDTO);

  StimmzettelOfTeamDTO toDTO(StimmzettelOfTeamModel stimmzettelOfTeamModel);
}
