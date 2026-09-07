package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StimmzettelRepository extends CrudRepository<Stimmzettel, StimmzettelID> {

  List<Stimmzettel> findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      String wahlbezirkID, String wahlID, String teamID);

  @Query("""
    DELETE FROM Stimmzettel stimmzettel
    WHERE stimmzettel.id.wahlbezirkID = :wahlbezirkID
      AND stimmzettel.id.wahlID = :wahlID
      AND stimmzettel.id.teamID = :teamID
    """
  )
  @Modifying
  void deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      @Param("wahlbezirkID") String wahlbezirkID, @Param("wahlID") String wahlID, @Param("teamID") String teamID);

  int countByIdWahlbezirkIDAndIdWahlID(String wahlbezirkID, String wahlID);
}
