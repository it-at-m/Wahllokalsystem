package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOfTeamModel;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;

@Mapper
public interface StimmzettelDTOMapper {

  StimmzettelOfTeamModel toModel(StimmzettelOfTeamDTO stimmzettelDTO);

  StimmzettelOfTeamDTO toDTO(StimmzettelOfTeamModel stimmzettelOfTeamModel);
}
