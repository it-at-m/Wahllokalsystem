package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StimmzettelerfassungStatusRepository
    extends CrudRepository<StimmzettelerfassungStatus, BezirkUndWahlID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "SELECT status FROM StimmzettelerfassungStatus status "
          + "WHERE status.bezirkUndWahlID = :bezirkUndWahlID")
  Optional<StimmzettelerfassungStatus> findByBezirkUndWahlIDForUpdate(
      @Param("bezirkUndWahlID") BezirkUndWahlID bezirkUndWahlID);
}
