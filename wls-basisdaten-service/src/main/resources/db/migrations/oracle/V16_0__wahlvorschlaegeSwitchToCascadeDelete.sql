DECLARE
    v_constraint_name user_constraints.constraint_name%TYPE;
BEGIN
    SELECT uc.constraint_name
    INTO v_constraint_name
    FROM user_constraints uc
             JOIN user_cons_columns ucc
                  ON uc.constraint_name = ucc.constraint_name
    WHERE uc.table_name = 'WAHLVORSCHLAG'
      AND uc.constraint_type = 'R'
      AND ucc.column_name = 'WAHLVORSCHLAEGEID';

    EXECUTE IMMEDIATE 'ALTER TABLE Wahlvorschlag DROP CONSTRAINT ' || v_constraint_name;
END;
/

ALTER TABLE wahlvorschlag
    ADD CONSTRAINT fk_wahlvorschlag_wahlvorschlaege
        FOREIGN KEY (wahlvorschlaegeID)
            REFERENCES wahlvorschlaege (id)
            ON DELETE CASCADE;

DECLARE
    v_constraint_name user_constraints.constraint_name%TYPE;
BEGIN
    SELECT uc.constraint_name
    INTO v_constraint_name
    FROM user_constraints uc
             JOIN user_cons_columns ucc
                  ON uc.constraint_name = ucc.constraint_name
    WHERE uc.table_name = 'KANDIDAT'
      AND uc.constraint_type = 'R'
      AND ucc.column_name = 'WAHLVORSCHLAGID';

    EXECUTE IMMEDIATE 'ALTER TABLE Kandidat DROP CONSTRAINT ' || v_constraint_name;
END;
/

ALTER TABLE kandidat
    ADD CONSTRAINT fk_kandidat_wahlvorschlag
        FOREIGN KEY (wahlvorschlagID)
            REFERENCES wahlvorschlag (id)
            ON DELETE CASCADE;