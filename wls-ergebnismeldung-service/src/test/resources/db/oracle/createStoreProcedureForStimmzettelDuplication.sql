-- example CALL duplicate_stimmzettel('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e','e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b','A', 201, 99);

CREATE OR REPLACE PROCEDURE duplicate_stimmzettel(
    p_wahlID IN VARCHAR2,
    p_wahlbezirkID IN VARCHAR2,
    p_teamID IN VARCHAR2,
    p_oldKennung In NUMBER,
    p_anzahlDuplikate IN NUMBER
)
AS
    v_currentMaxKennung NUMBER;
    v_newKennung        NUMBER;
    TYPE t_map IS TABLE OF VARCHAR2(255) INDEX BY VARCHAR2(255);
    v_map_wahlvorschlag t_map;

BEGIN
    -------------------------------------------------------------------------
    -- 1) Vorlage-Stimmzettel bestimmen (höchste Kennung)
    -------------------------------------------------------------------------
    SELECT MAX(stimmzettelkennung)
    INTO v_currentMaxKennung
    FROM Stimmzettel
    WHERE wahlbezirkID = p_wahlbezirkID
      AND wahlID       = p_wahlID
      AND teamID       = p_teamID;

    IF p_oldKennung IS NULL OR v_currentMaxKennung IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Kein Stimmzettel zum Duplizieren gefunden.');
    END IF;

    -------------------------------------------------------------------------
    -- 2) Schleife: Erzeuge n Duplikate
    -------------------------------------------------------------------------
    FOR i IN 1 .. p_anzahlDuplikate LOOP

        ---------------------------------------------------------------------
        -- 2.1) Neue Kennung bestimmen
        ---------------------------------------------------------------------
            SELECT NVL(MAX(stimmzettelkennung), 0) + 1
            INTO v_newKennung
            FROM Stimmzettel
            WHERE wahlbezirkID = p_wahlbezirkID
              AND wahlID       = p_wahlID
              AND teamID       = p_teamID;

            ---------------------------------------------------------------------
            -- 2.2) Stimmzettel kopieren
            ---------------------------------------------------------------------
            INSERT INTO Stimmzettel (
                wahlbezirkID, wahlID, teamID, stimmzettelkennung,
                invalideVotes, gueltigkeit, beschluss_pro, beschluss_contra, beschluss_text
            )
            SELECT
                wahlbezirkID, wahlID, teamID, v_newKennung,
                invalideVotes, gueltigkeit, beschluss_pro, beschluss_contra, beschluss_text
            FROM Stimmzettel
            WHERE wahlbezirkID = p_wahlbezirkID
              AND wahlID       = p_wahlID
              AND teamID       = p_teamID
              AND stimmzettelkennung = p_oldKennung;

            ---------------------------------------------------------------------
            -- 2.3) Wahlvorschläge kopieren + Mapping erzeugen
            ---------------------------------------------------------------------
            v_map_wahlvorschlag.DELETE;

            FOR w IN (
                SELECT id, wahlvorschlagID, selected
                FROM Wahlvorschlag
                WHERE stimmzettel_wahlbezirkID = p_wahlbezirkID
                  AND stimmzettel_wahlID       = p_wahlID
                  AND stimmzettel_teamID       = p_teamID
                  AND stimmzettel_stimmzettelkennung = p_oldKennung
                ) LOOP
                    DECLARE
                        v_newID VARCHAR2(255) := uuid_v4_formatted_sys_guid();
                    BEGIN
                        INSERT INTO Wahlvorschlag (
                            id, wahlvorschlagID, selected,
                            stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                            stimmzettel_teamID, stimmzettel_stimmzettelkennung
                        )
                        VALUES (
                                   v_newID,
                                   w.wahlvorschlagID,
                                   w.selected,
                                   p_wahlbezirkID,
                                   p_wahlID,
                                   p_teamID,
                                   v_newKennung
                               );

                        v_map_wahlvorschlag(w.id) := v_newID;


                        ---------------------------------------------------------------------
                        -- 2.4) Kandidaten kopieren
                        ---------------------------------------------------------------------
                        FOR k IN (
                            SELECT *
                            FROM Kandidat
                            WHERE wahlvorschlag_id = w.ID
                            ) LOOP
                                INSERT INTO Kandidat (
                                    id, kandidatID, nennungsNummer,
                                    wahlvorschlag_id, discarded,
                                    votesByVoter, invalidVotes, votesByWahlvorschlag
                                )
                                VALUES (
                                           uuid_v4_formatted_sys_guid(),
                                           k.kandidatID,
                                           k.nennungsNummer,
                                           v_newID,
                                           k.discarded,
                                           k.votesByVoter,
                                           k.invalidVotes,
                                           k.votesByWahlvorschlag
                                       );
                            END LOOP;
                    END;
                END LOOP;

            ---------------------------------------------------------------------
            -- 2.5) WahlvorstandBeschlussgrund kopieren
            ---------------------------------------------------------------------
            INSERT INTO WahlvorstandBeschlussgrund (
                id, text,
                stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                stimmzettel_teamID, stimmzettel_stimmzettelkennung
            )
            SELECT
                uuid_v4_formatted_sys_guid(),
                text,
                stimmzettel_wahlbezirkID,
                stimmzettel_wahlID,
                stimmzettel_teamID,
                v_newKennung
            FROM WahlvorstandBeschlussgrund
            WHERE stimmzettel_wahlbezirkID = p_wahlbezirkID
              AND stimmzettel_wahlID       = p_wahlID
              AND stimmzettel_teamID       = p_teamID
              AND stimmzettel_stimmzettelkennung = p_oldKennung;

            ---------------------------------------------------------------------
            -- 2.6) SystemBeschlussgrund kopieren
            ---------------------------------------------------------------------
            INSERT INTO SystemBeschlussgrund (
                id, reason,
                stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                stimmzettel_teamID, stimmzettel_stimmzettelkennung
            )
            SELECT
                uuid_v4_formatted_sys_guid(),
                reason,
                stimmzettel_wahlbezirkID,
                stimmzettel_wahlID,
                stimmzettel_teamID,
                v_newKennung
            FROM SystemBeschlussgrund
            WHERE stimmzettel_wahlbezirkID = p_wahlbezirkID
              AND stimmzettel_wahlID       = p_wahlID
              AND stimmzettel_teamID       = p_teamID
              AND stimmzettel_stimmzettelkennung = p_oldKennung;

        END LOOP;

    COMMIT;

END;
/