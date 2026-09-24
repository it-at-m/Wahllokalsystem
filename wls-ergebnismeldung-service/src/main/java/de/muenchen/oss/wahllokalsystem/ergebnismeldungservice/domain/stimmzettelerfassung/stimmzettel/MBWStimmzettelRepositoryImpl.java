package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MBWStimmzettelRepositoryImpl implements MBWStimmzettelRepository {

  private final MBWStapelStimmzettelRepository mbwStapelStimmzettelRepository;

  @Override
  public List<WahlvorschlagStimmzettelAnzahl> getStapelA(
      final String wahlID, final String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelA(wahlID, wahlbezirkID);
  }

  @Override
  public List<WahlvorschlagStimmzettelAnzahl> getStapelB(String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelB(wahlID, wahlbezirkID);
  }

  @Override
  public long getStapelD(String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelD(wahlID, wahlbezirkID);
  }

  @Override
  public List<KandidatStimmenAnzahl> getStapelBC(String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelBC(wahlID, wahlbezirkID);
  }
}

interface MBWStapelStimmzettelRepository extends StimmzettelRepository {
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
                        OR kandidat.votesByVoter > 0
                        OR kandidat.invalidVotes > 0
                      )
                  )
                GROUP BY selectedWahlvorschlag.wahlvorschlagID
                """)
  List<WahlvorschlagStimmzettelAnzahl> getStapelA(
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
  List<WahlvorschlagStimmzettelAnzahl> getStapelB(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  @Query(
      """
                SELECT COUNT(stimmzettel)
                FROM Stimmzettel stimmzettel
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.INVALID
                """)
  long getStapelD(@Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

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
                  AND (
                      (SELECT COUNT(wahlvorschlag)
                        FROM Wahlvorschlag wahlvorschlag
                        WHERE wahlvorschlag.stimmzettel = stimmzettel
                      ) > 1
                      OR EXISTS (
                        SELECT kandidat
                        FROM Kandidat kandidat
                        WHERE kandidat.wahlvorschlag = wahlvorschlag
                        AND (
                            kandidat.discarded = true
                            OR (kandidat.votesByVoter IS NOT NULL AND kandidat.votesByVoter <> 0)
                            OR (kandidat.invalidVotes IS NOT NULL AND kandidat.invalidVotes <> 0)
                        )
                      )
                  )
                GROUP BY wahlvorschlag.wahlvorschlagID,
                         kandidat.kandidatID.kandidatID
                """)
  List<KandidatStimmenAnzahl> getStapelBC(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);
}
