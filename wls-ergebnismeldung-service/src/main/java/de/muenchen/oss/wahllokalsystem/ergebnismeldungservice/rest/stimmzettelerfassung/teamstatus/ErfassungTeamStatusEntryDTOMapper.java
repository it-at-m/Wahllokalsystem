package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus.ErfassungTeamStatusEntryModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErfassungTeamStatusEntryDTOMapper {

  StimmzettelerfassungTeamStatusEntryDTO toDTO(ErfassungTeamStatusEntryModel model);
}
