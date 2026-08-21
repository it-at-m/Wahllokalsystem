package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StimmzettelerfassungTeamStatusRepository
    extends CrudRepository<StimmzettelerfassungTeamStatus, TeamBezirkUndWahlID> {
  List<StimmzettelerfassungTeamStatus> findByIdWahlIDAndIdWahlbezirkID(
      String wahlID, String wahlbezirkID);
}
