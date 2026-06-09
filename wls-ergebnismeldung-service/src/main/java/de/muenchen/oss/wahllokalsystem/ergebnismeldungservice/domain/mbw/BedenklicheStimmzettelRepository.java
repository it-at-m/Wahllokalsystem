package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BedenklicheStimmzettelRepository
    extends CrudRepository<BedenklicheStimmzettel, BezirkUndWahlID> {

  @Query(
      "SELECT S FROM BedenklicheStimmzettel S JOIN FETCH S.bedenklicheStimmzettels S2 WHERE S.compositeId.wahlbezirkID = :wahlbezirkID and S.compositeId.wahlID = :wahlID ORDER BY S2.compositeId.orderIndex ASC")
  Optional<BedenklicheStimmzettel> findByBezirkUndWahlIDOrderbyOrderIndexAsc(
      @Param("wahlbezirkID") final String wahlbezirkId, @Param("wahlID") final String wahlID);
}
