package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.TeamErfassungStatusModel;
import org.mapstruct.Mapper;

@Mapper
public interface TeamErfassungStatusDTOMapper {

  TeamErfassungStatusDTO toDTO(TeamErfassungStatusModel teamErfassungStatusModel);

  TeamErfassungStatusModel toModel(TeamErfassungStatusDTO teamErfassungStatusDTO);
}
