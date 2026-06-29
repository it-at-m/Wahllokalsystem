ALTER TABLE kandidat
    ADD anzahlNennungen NUMBER DEFAULT 1;

UPDATE kandidat
SET anzahlNennungen = 2
where name = 'Kimberly Price';

UPDATE kandidat
SET anzahlNennungen = 3
where name = 'Sofia Kim';

ALTER TABLE kandidat
    MODIFY anzahlNennungen NOT NULL;

ALTER TABLE kandidat
    ADD CONSTRAINT chk_anzahlNennungen_min1 CHECK (anzahlNennungen >= 1);