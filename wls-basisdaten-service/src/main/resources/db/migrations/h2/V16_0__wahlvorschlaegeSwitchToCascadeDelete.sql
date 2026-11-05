SET @constraint_name = (
    SELECT tc.constraint_name
    FROM information_schema.table_constraints tc
             JOIN information_schema.key_column_usage kcu
                  ON tc.constraint_name = kcu.constraint_name
                      AND tc.table_schema = kcu.table_schema
    WHERE tc.table_name = 'WAHLVORSCHLAG'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND kcu.column_name = 'WAHLVORSCHLAEGEID'
    );

EXECUTE IMMEDIATE 'ALTER TABLE Wahlvorschlag DROP CONSTRAINT ' || @constraint_name;

ALTER TABLE wahlvorschlag
    ADD CONSTRAINT fk_wahlvorschlag_wahlvorschlaege
        FOREIGN KEY (wahlvorschlaegeID)
            REFERENCES wahlvorschlaege (id)
            ON DELETE CASCADE;

SET @constraint_name = (
    SELECT tc.constraint_name
    FROM information_schema.table_constraints tc
             JOIN information_schema.key_column_usage kcu
                  ON tc.constraint_name = kcu.constraint_name
                      AND tc.table_schema = kcu.table_schema
    WHERE tc.table_name = 'KANDIDAT'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND kcu.column_name = 'WAHLVORSCHLAGID'
    );

EXECUTE IMMEDIATE 'ALTER TABLE Kandidat DROP CONSTRAINT ' || @constraint_name;

ALTER TABLE kandidat
    ADD CONSTRAINT fk_kandidat_wahlvorschlag
        FOREIGN KEY (wahlvorschlagID)
            REFERENCES wahlvorschlag (id)
            ON DELETE CASCADE;