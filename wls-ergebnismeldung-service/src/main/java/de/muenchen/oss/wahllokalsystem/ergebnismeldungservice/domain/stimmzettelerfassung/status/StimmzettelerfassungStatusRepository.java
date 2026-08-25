package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.springframework.data.repository.CrudRepository;

public interface StimmzettelerfassungStatusRepository
    extends CrudRepository<StimmzettelerfassungStatus, BezirkUndWahlID> {}
