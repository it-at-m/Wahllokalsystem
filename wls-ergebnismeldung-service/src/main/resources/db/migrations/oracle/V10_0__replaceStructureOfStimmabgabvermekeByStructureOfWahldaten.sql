ALTER TABLE Wahldaten
    DROP CONSTRAINT FK_WD;
ALTER TABLE WAHLDATEN
    ADD CONSTRAINT NATURAL_ID UNIQUE (WAHLID, WAHLBEZIRKID, WAEHLERVERZEICHNISNUMMER);

DROP TABLE Stimmabgabevermerke;

ALTER TABLE Wahldaten RENAME TO Stimmabgabevermerke;

ALTER TABLE Vermerk RENAME COLUMN wahldatenID to stimmabgabevermerkeID;

ALTER TABLE Vermerk
    DROP CONSTRAINT fk_Wahldaten;

ALTER TABLE EingenommeneWahlscheine
    DROP CONSTRAINT fk_Wahldaten_ew;

ALTER TABLE EingenommeneWahlscheine
    ADD fk_wahlbezirkID VARCHAR(1024);

ALTER TABLE EingenommeneWahlscheine
    ADD fk_wahlID VARCHAR(1024);

ALTER TABLE EingenommeneWahlscheine
    ADD fk_waehlerverzeichnisNummer NUMBER(19, 0);

UPDATE EingenommeneWahlscheine ew
SET fk_wahlbezirkID             = (SELECT sav.wahlbezirkID
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = ew.wahldatenID),
    fk_wahlID                   = (SELECT sav.wahlID
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = ew.wahldatenID),
    fk_waehlerverzeichnisNummer = (SELECT sav.waehlerverzeichnisNummer
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = ew.wahldatenID);

ALTER TABLE EingenommeneWahlscheine
    MODIFY fk_wahlbezirkID NOT NULL;

ALTER TABLE EingenommeneWahlscheine
    MODIFY fk_wahlID NOT NULL;

ALTER TABLE EingenommeneWahlscheine
    MODIFY fk_waehlerverzeichnisNummer NOT NULL;

ALTER TABLE Vermerk
    ADD fk_wahlbezirkID VARCHAR(1024);

ALTER TABLE Vermerk
    ADD fk_wahlID VARCHAR(1024);

ALTER TABLE Vermerk
    ADD fk_waehlerverzeichnisNummer NUMBER(19, 0);

UPDATE Vermerk v
SET fk_wahlbezirkID             = (SELECT sav.wahlbezirkID
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = v.stimmabgabevermerkeID),
    fk_wahlID                   = (SELECT sav.wahlID
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = v.stimmabgabevermerkeID),
    fk_waehlerverzeichnisNummer = (SELECT sav.waehlerverzeichnisNummer
                                   FROM Stimmabgabevermerke sav
                                   WHERE sav.id = v.stimmabgabevermerkeID);

ALTER TABLE Vermerk
    MODIFY fk_wahlbezirkID NOT NULL;

ALTER TABLE Vermerk
    MODIFY fk_wahlID NOT NULL;

ALTER TABLE Vermerk
    MODIFY fk_waehlerverzeichnisNummer NOT NULL;

ALTER TABLE Stimmabgabevermerke
    DROP PRIMARY KEY;

ALTER TABLE Stimmabgabevermerke
    DROP CONSTRAINT NATURAL_ID;

ALTER TABLE Stimmabgabevermerke
    ADD PRIMARY KEY (wahlbezirkID, wahlID, waehlerverzeichnisNummer);

ALTER TABLE Vermerk
    ADD CONSTRAINT fk_Stimmabgabevermerke_Vermerk
        FOREIGN KEY (fk_wahlbezirkID, fk_wahlID, fk_waehlerverzeichnisNummer)
            REFERENCES Stimmabgabevermerke (wahlbezirkID, wahlID, waehlerverzeichnisNummer)
                ON DELETE CASCADE;

ALTER TABLE EingenommeneWahlscheine
    ADD CONSTRAINT fk_Stimmabgabevermerke_Ew
        FOREIGN KEY (fk_wahlbezirkID, fk_wahlID, fk_waehlerverzeichnisNummer)
            REFERENCES Stimmabgabevermerke (wahlbezirkID, wahlID, waehlerverzeichnisNummer)
                ON DELETE CASCADE;

ALTER TABLE Vermerk
    DROP COLUMN stimmabgabevermerkeID;

ALTER TABLE EingenommeneWahlscheine
    DROP COLUMN wahldatenID;

ALTER TABLE Stimmabgabevermerke
    DROP COLUMN id;
