package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BedenklicheStimmzettelRepository
    extends CrudRepository<BedenklicheStimmzettelErfassung, BezirkUndWahlID> {

  @Query(
      "SELECT S FROM BedenklicheStimmzettelErfassung S JOIN FETCH S.bedenklicheStimmzettel S2 WHERE S.compositeId.wahlbezirkID = :wahlbezirkID and S.compositeId.wahlID = :wahlID ORDER BY S2.compositeId.orderIndex ASC")
  Optional<BedenklicheStimmzettelErfassung> findByBezirkUndWahlIDOrderbyOrderIndexAsc(
      @Param("wahlbezirkID") final String wahlbezirkId, @Param("wahlID") final String wahlID);
}
