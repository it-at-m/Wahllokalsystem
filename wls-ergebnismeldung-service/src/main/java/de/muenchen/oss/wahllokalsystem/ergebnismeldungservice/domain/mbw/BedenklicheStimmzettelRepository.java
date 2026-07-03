package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BedenklicheStimmzettelRepository
    extends CrudRepository<BedenklicheStimmzettelErfassung, BezirkUndWahlID> {

  @Query(
      "SELECT DISTINCT S FROM BedenklicheStimmzettelErfassung S LEFT JOIN FETCH S.bedenklicheStimmzettel S2 WHERE S.compositeId.wahlbezirkID = :wahlbezirkID and S.compositeId.wahlID = :wahlID ORDER BY S2.compositeId.orderIndex ASC")
  Optional<BedenklicheStimmzettelErfassung> findByBezirkUndWahlIDOrderbyOrderIndexAsc(
      @Param("wahlbezirkID") final String wahlbezirkId, @Param("wahlID") final String wahlID);

  @Query(
      "SELECT COUNT(*) FROM BedenklicheStimmzettelErfassung S JOIN BedenklicherStimmzettel S2 ON S.compositeId.wahlbezirkID = S2.compositeId.wahlbezirkID AND S.compositeId.wahlID = S2.compositeId.wahlID WHERE S.compositeId.wahlbezirkID = :wahlbezirkID and S.compositeId.wahlID = :wahlID and S2.validity = 'INVALID'")
  long countInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID(
      @Param("wahlbezirkID") final String wahlbezirkId, @Param("wahlID") final String wahlID);
}
