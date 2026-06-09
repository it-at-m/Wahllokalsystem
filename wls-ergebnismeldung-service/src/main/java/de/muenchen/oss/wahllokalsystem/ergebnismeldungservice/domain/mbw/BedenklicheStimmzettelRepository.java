package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BedenklicheStimmzettelRepository
    extends JpaRepository<BedenklicheStimmzettel, BezirkIdWahlIdOrderIndex> {

  @Query(
      "SELECT S FROM BedenklicheStimmzettel S WHERE S.compositeId.wahlbezirkID = :wahlbezirkID and S.compositeId.wahlID = :wahlID ORDER BY S.compositeId.orderIndex ASC")
  Collection<BedenklicheStimmzettel> findByBezirkUndWahlIDOrderbyOrderIndexAsc(
      @Param("wahlbezirkID") final String wahlbezirkId, @Param("wahlID") final String wahlID);
}
