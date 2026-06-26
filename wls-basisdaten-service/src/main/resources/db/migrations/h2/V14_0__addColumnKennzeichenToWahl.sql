ALTER TABLE Wahl
    ADD COLUMN kennzeichen VARCHAR(5);

UPDATE Wahl SET kennzeichen = UPPER(SUBSTRING(wahlart, 1, 1));

ALTER TABLE Wahl
    ALTER COLUMN kennzeichen SET NOT NULL;
