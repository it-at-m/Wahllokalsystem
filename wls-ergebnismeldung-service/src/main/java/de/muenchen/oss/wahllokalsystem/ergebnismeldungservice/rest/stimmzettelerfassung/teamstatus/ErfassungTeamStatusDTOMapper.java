package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErfassungTeamStatusDTOMapper {

  ErfassungTeamStatusDTO toDTO(ErfassungTeamStatusModel erfassungTeamStatusModel);

  @Mapping(target = "status", source = ".")
  StimmzettelerfassungTeamStatusDTO toStimmzettelerfassungTeamStatusDTO(
      ErfassungTeamStatusModel erfassungTeamStatusModel);

  ErfassungTeamStatusModel toModel(ErfassungTeamStatusDTO erfassungTeamStatusDTO);
}
