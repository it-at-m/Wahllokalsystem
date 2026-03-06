package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel;

import java.util.Collection;
import org.springframework.data.repository.CrudRepository;

public interface StimmzettelRepository extends CrudRepository<Stimmzettel, BezirkIDWahlIDNummer> {
  Collection<Stimmzettel> findByCombinedId_WahlIDAndCombinedId_WahlbezirkID(
      String combinedIdWahlID, String combinedIdWahlbezirkID);
}
