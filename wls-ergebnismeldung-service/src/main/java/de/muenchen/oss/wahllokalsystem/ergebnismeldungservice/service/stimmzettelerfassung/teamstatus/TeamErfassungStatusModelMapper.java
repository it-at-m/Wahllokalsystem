package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import org.mapstruct.Mapper;

@Mapper
public interface TeamErfassungStatusModelMapper {

  TeamErfassungStatusModel toModel(TeamErfassungStatus teamStatus);

  StimmzettelerfassungTeamStatus toEntity(
      TeamBezirkUndWahlIDModel id, TeamErfassungStatusModel status);

  TeamBezirkUndWahlID toEntity(TeamBezirkUndWahlIDModel id);
}
