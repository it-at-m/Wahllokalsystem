package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StimmzettelRepository extends CrudRepository<Stimmzettel, StimmzettelID> {

  List<Stimmzettel> findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      String wahlbezirkID, String wahlID, String teamID);

  @Query(
      """
                DELETE FROM Stimmzettel stimmzettel
                WHERE stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.teamID = :teamID
                """)
  @Modifying
  void deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
      @Param("wahlbezirkID") String wahlbezirkID,
      @Param("wahlID") String wahlID,
      @Param("teamID") String teamID);

  int countByIdWahlbezirkIDAndIdWahlID(String wahlbezirkID, String wahlID);

  @Query(
      """
                SELECT selectedWahlvorschlag.wahlvorschlagID AS wahlvorschlagID,
                       COUNT(stimmzettel) AS anzahl
                FROM Stimmzettel stimmzettel
                JOIN stimmzettel.wahlvorschlaege selectedWahlvorschlag
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.VALID
                  AND stimmzettel.invalideVotes = 0
                  AND selectedWahlvorschlag.selected = true
                  AND (
                    SELECT COUNT(wahlvorschlag)
                    FROM Wahlvorschlag wahlvorschlag
                    WHERE wahlvorschlag.stimmzettel = stimmzettel
                  ) = 1
                  AND NOT EXISTS (
                    SELECT kandidat
                    FROM Kandidat kandidat
                    WHERE kandidat.wahlvorschlag = selectedWahlvorschlag
                      AND (
                        kandidat.discarded = true
                        OR kandidat.votesByVoter IS NOT NULL AND kandidat.votesByVoter <> 0
                        OR kandidat.invalidVotes IS NOT NULL AND kandidat.invalidVotes <> 0
                      )
                  )
                GROUP BY selectedWahlvorschlag.wahlvorschlagID
                """)
  List<WahlvorschlagStimmzettelAnzahl>
      getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
          @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  @Query(
      """
                SELECT selectedWahlvorschlag.wahlvorschlagID AS wahlvorschlagID,
                       COUNT(stimmzettel) AS anzahl
                FROM Stimmzettel stimmzettel
                JOIN stimmzettel.wahlvorschlaege selectedWahlvorschlag
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.VALID
                  AND (
                    SELECT COUNT(wahlvorschlag)
                    FROM Wahlvorschlag wahlvorschlag
                    WHERE wahlvorschlag.stimmzettel = stimmzettel
                  ) = 1
                  AND EXISTS (
                    SELECT kandidat
                    FROM Kandidat kandidat
                    WHERE kandidat.wahlvorschlag = selectedWahlvorschlag
                      AND (
                        kandidat.discarded = true
                        OR kandidat.votesByVoter > 0
                        OR kandidat.invalidVotes > 0
                      )
                  )
                GROUP BY selectedWahlvorschlag.wahlvorschlagID
                """)
  List<WahlvorschlagStimmzettelAnzahl>
      getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
          @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  @Query(
      """
                SELECT COUNT(stimmzettel)
                FROM Stimmzettel stimmzettel
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.INVALID
                """)
  long countInvalidStimmzettel(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  @Query(
      """
                SELECT wahlvorschlag.wahlvorschlagID AS wahlvorschlagID,
                       kandidat.kandidatID.kandidatID AS kandidatID,
                       SUM(COALESCE(kandidat.votesByVoter, 0) + COALESCE(kandidat.votesByWahlvorschlag, 0)) AS anzahl
                FROM Stimmzettel stimmzettel
                JOIN stimmzettel.wahlvorschlaege wahlvorschlag
                JOIN wahlvorschlag.kandidaten kandidat
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.VALID
                  AND EXISTS (
                    SELECT kandidat
                    FROM Kandidat kandidat
                    WHERE kandidat.wahlvorschlag = wahlvorschlag
                      AND (
                        kandidat.discarded = true
                        OR kandidat.votesByVoter IS NOT NULL AND kandidat.votesByVoter <> 0
                        OR kandidat.votesByWahlvorschlag IS NOT NULL AND kandidat.votesByWahlvorschlag <> 0
                      )
                  )
                GROUP BY wahlvorschlag.wahlvorschlagID,
                         kandidat.kandidatID.kandidatID
                """)
  List<KandidatStimmenAnzahl>
      getValidKandidatenVotesPerWahlvorschlagWhereAtLeast2WahlvorschlaegeAreSelectedOrAtLeastOneKandidatHasNotOnlyReststimmen(
          @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);
}
