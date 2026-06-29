ALTER TABLE kandidat
    ADD anzahlNennungen INT DEFAULT 1;

UPDATE kandidat
SET anzahlNennungen = 2
WHERE name = 'Kimberly Price';

UPDATE kandidat
SET anzahlNennungen = 3
WHERE name = 'Sofia Kim';

ALTER TABLE kandidat
ALTER COLUMN anzahlNennungen SET NOT NULL;

ALTER TABLE kandidat
    ADD CONSTRAINT chk_anzahlNennungen_min1 CHECK (anzahlNennungen >= 1);