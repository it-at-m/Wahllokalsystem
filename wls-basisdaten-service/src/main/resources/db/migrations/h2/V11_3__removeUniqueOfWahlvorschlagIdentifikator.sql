ALTER TABLE Wahlvorschlag
    ADD identifikator2 VARCHAR(1024) NOT NULL;
UPDATE Wahlvorschlag
SET Wahlvorschlag.identifikator2 = Wahlvorschlag.identifikator;
ALTER TABLE Wahlvorschlag
    DROP COLUMN identifikator;

ALTER TABLE Wahlvorschlag
    ADD identifikator VARCHAR(1024) NOT NULL;
UPDATE Wahlvorschlag
    SET Wahlvorschlag.identifikator = Wahlvorschlag.identifikator2;
ALTER TABLE Wahlvorschlag
    DROP COLUMN identifikator2;