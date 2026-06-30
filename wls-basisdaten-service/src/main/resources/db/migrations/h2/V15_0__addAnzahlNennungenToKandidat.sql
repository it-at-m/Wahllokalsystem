ALTER TABLE kandidat
    ADD anzahlNennungen INT DEFAULT 1;

ALTER TABLE kandidat
ALTER COLUMN anzahlNennungen SET NOT NULL;

ALTER TABLE kandidat
    ADD CONSTRAINT chk_anzahlNennungen_min1 CHECK (anzahlNennungen >= 1);