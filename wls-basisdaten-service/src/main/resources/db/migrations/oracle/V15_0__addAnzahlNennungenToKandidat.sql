ALTER TABLE kandidat
    ADD anzahlNennungen NUMBER DEFAULT 1;

ALTER TABLE kandidat
    MODIFY anzahlNennungen NOT NULL;

ALTER TABLE kandidat
    ADD CONSTRAINT chk_anzahlNennungen_min1 CHECK (anzahlNennungen >= 1);