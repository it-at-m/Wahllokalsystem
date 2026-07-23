package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus;

import org.springframework.data.repository.CrudRepository;

public interface StimmzettelerfassungTeamStatusRepository
    extends CrudRepository<StimmzettelerfassungTeamStatus, TeamBezirkUndWahlID> {}
