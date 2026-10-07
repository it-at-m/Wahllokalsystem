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
  public List<KandidatStimmenAnzahl> getStapelBGroupedByKandidat(
      String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);
  }

  @Override
  public List<WahlvorschlagStimmzettelAnzahl> getStapelBGroupedByWahlvorschlag(
      String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);
  }

  @Override
  public List<KandidatStimmenAnzahl> getStapelC(String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelC(wahlID, wahlbezirkID);
  }

  @Override
  public long getStapelD(String wahlID, String wahlbezirkID) {
    return mbwStapelStimmzettelRepository.getStapelD(wahlID, wahlbezirkID);
  }
}

interface MBWStapelStimmzettelRepository extends StimmzettelRepository {
  String STAPEL_B_CONDITION =
      """
                    ( SELECT COUNT(wahlvorschlag)
                      FROM Wahlvorschlag wahlvorschlag
                      WHERE wahlvorschlag.stimmzettel = stimmzettel
                    ) = 1
                    AND (EXISTS (
                      SELECT kandidat
                      FROM Kandidat kandidat
                      WHERE kandidat.wahlvorschlag = selectedWahlvorschlag
                        AND (
                          kandidat.discarded = true
                          OR kandidat.votesByVoter > 0
                          OR kandidat.invalidVotes > 0
                        )
                      OR stimmzettel.invalideVotes > 0
                    ))
            """;

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
                       kandidat.kandidatID.kandidatID AS kandidatID,
                       SUM(COALESCE(kandidat.votesByVoter, 0) + COALESCE(kandidat.votesByWahlvorschlag, 0)) AS anzahl
                FROM Stimmzettel stimmzettel
                JOIN stimmzettel.wahlvorschlaege selectedWahlvorschlag
                JOIN selectedWahlvorschlag.kandidaten kandidat
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit = de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.VALID
                  AND
                """
          + STAPEL_B_CONDITION
          + """
                        GROUP BY selectedWahlvorschlag.wahlvorschlagID,
                                 kandidat.kandidatID.kandidatID
                        """)
  List<KandidatStimmenAnzahl> getStapelBGroupedByKandidat(
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
                  AND
                """
          + STAPEL_B_CONDITION
          + """
                        GROUP BY selectedWahlvorschlag.wahlvorschlagID
                        """)
  List<WahlvorschlagStimmzettelAnzahl> getStapelBGroupedByWahlvorschlag(
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
                  AND (
                      (SELECT COUNT(wahlvorschlag)
                        FROM Wahlvorschlag wahlvorschlag
                        WHERE wahlvorschlag.stimmzettel = stimmzettel
                      ) > 1
                  )
                GROUP BY wahlvorschlag.wahlvorschlagID,
                         kandidat.kandidatID.kandidatID
                """)
  List<KandidatStimmenAnzahl> getStapelC(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  @Query(
      """
                SELECT COUNT(stimmzettel)
                FROM Stimmzettel stimmzettel
                WHERE stimmzettel.id.wahlID = :wahlID
                  AND stimmzettel.id.wahlbezirkID = :wahlbezirkID
                  AND stimmzettel.gueltigkeit IN (
                                  de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.INVALID,
                                  de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.LEER,
                                  de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit.BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG)
                """)
  long getStapelD(@Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);
}
