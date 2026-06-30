ALTER TABLE Wahl
    ADD kennzeichen VARCHAR(5);

UPDATE Wahl SET kennzeichen = UPPER(SUBSTR(wahlart, 1, 1));

ALTER TABLE Wahl
    MODIFY kennzeichen NOT NULL;
