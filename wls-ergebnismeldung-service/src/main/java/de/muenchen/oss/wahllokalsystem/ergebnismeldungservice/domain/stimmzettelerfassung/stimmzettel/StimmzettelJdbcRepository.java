package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.logging.PerformanceLogging;
import java.sql.Types;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StimmzettelJdbcRepository {

    private static final int BATCH_SIZE = 100;
    private static final Logger deletePerformanceLogger = PerformanceLogging.createPerformanceLogger(StimmzettelJdbcRepository.class.getName() + ".delete");
    private static final Logger insertStimmzettelPerformanceLogger = PerformanceLogging.createPerformanceLogger(StimmzettelJdbcRepository.class.getName() + ".insertStimmzettel");
    private static final Logger insertWahlvorschlaegePerformanceLogger = PerformanceLogging.createPerformanceLogger(StimmzettelJdbcRepository.class.getName() + ".insertWahlvorschlaege");
    private static final Logger inserKandidatendeletePerformanceLogger = PerformanceLogging.createPerformanceLogger(StimmzettelJdbcRepository.class.getName() + ".insertKandidaten");

    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public void replaceStimmzettel(
            final String wahlbezirkID,
            final String wahlID,
            final String teamID,
            final Collection<Stimmzettel> stimmzettel) {
        deleteStimmzettel(wahlbezirkID, wahlID, teamID);

        insertStimmzettel(stimmzettel);
        insertWahlvorschlaege(stimmzettel);
        insertKandidaten(stimmzettel);
        insertSystemBeschlussgruende(stimmzettel);
        insertWahlvorstandBeschlussgruende(stimmzettel);
    }

    private void deleteStimmzettel(
            final String wahlbezirkID, final String wahlID, final String teamID) {
        val stopWatch = StopWatch.createStarted();
        jdbcTemplate.update(
                """
                DELETE FROM Stimmzettel
                WHERE wahlbezirkID = ?
                  AND wahlID = ?
                  AND teamID = ?
                """,
                wahlbezirkID,
                wahlID,
                teamID);
        stopWatch.stop();
        deletePerformanceLogger.info("Delete Stimmzettel took {} ms", stopWatch.getDuration().toMillis());
    }

    private void insertStimmzettel(final Collection<Stimmzettel> stimmzettel) {
        val stopWatch = StopWatch.createStarted();
        jdbcTemplate.batchUpdate(
                """
                INSERT INTO Stimmzettel (
                  wahlbezirkID,
                  wahlID,
                  teamID,
                  stimmzettelkennung,
                  invalideVotes,
                  gueltigkeit,
                  beschluss_pro,
                  beschluss_contra,
                  beschluss_text
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                stimmzettel,
                BATCH_SIZE,
                (ps, entity) -> {
                    val id = entity.getId();
                    val beschlussfassung = entity.getBeschlussfassung();

                    ps.setString(1, id.getWahlbezirkID());
                    ps.setString(2, id.getWahlID());
                    ps.setString(3, id.getTeamID());
                    ps.setInt(4, id.getStimmzettelkennung());
                    ps.setInt(5, entity.getInvalideVotes());
                    ps.setString(6, entity.getGueltigkeit().name());

                    if (beschlussfassung == null) {
                        ps.setNull(7, Types.INTEGER);
                        ps.setNull(8, Types.INTEGER);
                        ps.setNull(9, Types.CLOB);
                    } else {
                        ps.setInt(7, beschlussfassung.getPro());
                        ps.setInt(8, beschlussfassung.getContra());
                        ps.setString(9, beschlussfassung.getText());
                    }
                });
        stopWatch.stop();
        insertStimmzettelPerformanceLogger.info("Insert Stimmzettel took {} ms", stopWatch.getDuration().toMillis());
    }

    private void insertWahlvorschlaege(final Collection<Stimmzettel> stimmzettel) {
        val stopWatch = StopWatch.createStarted();

        val wahlvorschlaege =
                stimmzettel.stream()
                        .flatMap(entity -> entity.getWahlvorschlaege().stream())
                        .toList();

        wahlvorschlaege.forEach(
                wahlvorschlag -> {
                    if (wahlvorschlag.getId() == null) {
                        wahlvorschlag.setId(UUID.randomUUID());
                    }
                });

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO Wahlvorschlag (
                  id,
                  wahlvorschlagID,
                  selected,
                  stimmzettel_wahlbezirkID,
                  stimmzettel_wahlID,
                  stimmzettel_teamID,
                  stimmzettel_stimmzettelkennung
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                wahlvorschlaege,
                BATCH_SIZE,
                (ps, wahlvorschlag) -> {
                    val stimmzettelId = wahlvorschlag.getStimmzettel().getId();

                    ps.setString(1, wahlvorschlag.getId().toString());
                    ps.setString(2, wahlvorschlag.getWahlvorschlagID());
                    ps.setBoolean(3, wahlvorschlag.isSelected());
                    ps.setString(4, stimmzettelId.getWahlbezirkID());
                    ps.setString(5, stimmzettelId.getWahlID());
                    ps.setString(6, stimmzettelId.getTeamID());
                    ps.setInt(7, stimmzettelId.getStimmzettelkennung());
                });
        stopWatch.stop();
        insertWahlvorschlaegePerformanceLogger.info("Insert Wahlvorschlaege took {} ms", stopWatch.getDuration().toMillis());
    }

    private void insertKandidaten(final Collection<Stimmzettel> stimmzettel) {
        val stopWatch = StopWatch.createStarted();

        val kandidaten =
                stimmzettel.stream()
                        .flatMap(entity -> entity.getWahlvorschlaege().stream())
                        .flatMap(wahlvorschlag -> wahlvorschlag.getKandidaten().stream())
                        .toList();

        kandidaten.forEach(
                kandidat -> {
                    if (kandidat.getId() == null) {
                        kandidat.setId(UUID.randomUUID());
                    }
                });

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO Kandidat (
                  id,
                  kandidatID,
                  nennungsNummer,
                  wahlvorschlag_id,
                  discarded,
                  votesByVoter,
                  invalidVotes,
                  votesByWahlvorschlag
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                kandidaten,
                BATCH_SIZE,
                (ps, kandidat) -> {
                    val kandidatID = kandidat.getKandidatID();

                    ps.setString(1, kandidat.getId().toString());
                    ps.setString(2, kandidatID.getKandidatID());
                    ps.setInt(3, kandidatID.getNennungsNummer());
                    ps.setString(4, kandidat.getWahlvorschlag().getId().toString());
                    ps.setBoolean(5, kandidat.isDiscarded());
                    setNullableInteger(ps, 6, kandidat.getVotesByVoter());
                    setNullableInteger(ps, 7, kandidat.getInvalidVotes());
                    setNullableInteger(ps, 8, kandidat.getVotesByWahlvorschlag());
                });
        stopWatch.stop();
        inserKandidatendeletePerformanceLogger.info("Insert Kandidaten took {} ms", stopWatch.getDuration().toMillis());
    }

    private void insertSystemBeschlussgruende(final Collection<Stimmzettel> stimmzettel) {
        val systemBeschlussgruende =
                stimmzettel.stream()
                        .flatMap(entity -> entity.getSystemBeschlussvorschlag().stream())
                        .toList();

        systemBeschlussgruende.forEach(
                beschlussgrund -> {
                    if (beschlussgrund.getId() == null) {
                        beschlussgrund.setId(UUID.randomUUID());
                    }
                });

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO SystemBeschlussgrund (
                  id,
                  reason,
                  stimmzettel_wahlbezirkID,
                  stimmzettel_wahlID,
                  stimmzettel_teamID,
                  stimmzettel_stimmzettelkennung
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                systemBeschlussgruende,
                BATCH_SIZE,
                (ps, beschlussgrund) -> {
                    val stimmzettelId = beschlussgrund.getStimmzettel().getId();

                    ps.setString(1, beschlussgrund.getId().toString());
                    ps.setString(2, beschlussgrund.getReason().name());
                    ps.setString(3, stimmzettelId.getWahlbezirkID());
                    ps.setString(4, stimmzettelId.getWahlID());
                    ps.setString(5, stimmzettelId.getTeamID());
                    ps.setInt(6, stimmzettelId.getStimmzettelkennung());
                });
    }

    private void insertWahlvorstandBeschlussgruende(final Collection<Stimmzettel> stimmzettel) {
        val wahlvorstandBeschlussgruende =
                stimmzettel.stream()
                        .flatMap(entity -> entity.getWahlvorstandBeschlussvorschlag().stream())
                        .toList();

        wahlvorstandBeschlussgruende.forEach(
                beschlussgrund -> {
                    if (beschlussgrund.getId() == null) {
                        beschlussgrund.setId(UUID.randomUUID());
                    }
                });

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO WahlvorstandBeschlussgrund (
                  id,
                  text,
                  stimmzettel_wahlbezirkID,
                  stimmzettel_wahlID,
                  stimmzettel_teamID,
                  stimmzettel_stimmzettelkennung
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                wahlvorstandBeschlussgruende,
                BATCH_SIZE,
                (ps, beschlussgrund) -> {
                    val stimmzettelId = beschlussgrund.getStimmzettel().getId();

                    ps.setString(1, beschlussgrund.getId().toString());
                    ps.setString(2, beschlussgrund.getText());
                    ps.setString(3, stimmzettelId.getWahlbezirkID());
                    ps.setString(4, stimmzettelId.getWahlID());
                    ps.setString(5, stimmzettelId.getTeamID());
                    ps.setInt(6, stimmzettelId.getStimmzettelkennung());
                });
    }

    private static void setNullableInteger(
            final java.sql.PreparedStatement ps, final int parameterIndex, final Integer value)
            throws java.sql.SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, value);
        }
    }
}