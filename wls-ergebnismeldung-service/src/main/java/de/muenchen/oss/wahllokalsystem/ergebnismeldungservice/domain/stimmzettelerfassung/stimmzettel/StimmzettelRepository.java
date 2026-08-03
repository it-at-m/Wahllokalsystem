package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StimmzettelRepository extends CrudRepository<DSEStimmzettel, StimmzettelID> {

  List<DSEStimmzettel> findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      String wahlbezirkID, String wahlID, String teamID);

  void deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      String wahlbezirkID, String wahlID, String teamID);

  int countByIdWahlbezirkIDAndIdWahlID(String wahlbezirkID, String wahlID);
}
