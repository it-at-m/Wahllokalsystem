package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.ErfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import org.mapstruct.Mapper;

@Mapper
public interface ErfassungTeamStatusModelMapper {

  ErfassungTeamStatusModel toModel(ErfassungTeamStatus teamStatus);

  StimmzettelerfassungTeamStatus toEntity(
      TeamBezirkUndWahlIDModel id, ErfassungTeamStatusModel status);

  TeamBezirkUndWahlID toEntity(TeamBezirkUndWahlIDModel id);

  /** Maps full entity to service entry model including teamID and mapped status. */
  default ErfassungTeamStatusEntryModel toEntryModel(final StimmzettelerfassungTeamStatus entity) {
    if (entity == null || entity.getId() == null) {
      return null;
    }
    return new ErfassungTeamStatusEntryModel(
        entity.getId().getTeamID(), toModel(entity.getStatus()));
  }
}
