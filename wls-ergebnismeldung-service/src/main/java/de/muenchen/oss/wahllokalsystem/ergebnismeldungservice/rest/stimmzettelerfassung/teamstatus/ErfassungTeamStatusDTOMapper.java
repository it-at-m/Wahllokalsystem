package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
import org.mapstruct.Mapper;

@Mapper
public interface ErfassungTeamStatusDTOMapper {

  ErfassungTeamStatusDTO toDTO(ErfassungTeamStatusModel erfassungTeamStatusModel);

  ErfassungTeamStatusModel toModel(ErfassungTeamStatusDTO erfassungTeamStatusDTO);
}
