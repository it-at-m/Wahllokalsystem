DROP TABLE Kandidat;

TRUNCATE TABLE WAHLVORSCHLAG;
UPDATE Stimmzettel SET GUELTIGKEIT = 'LEER';

CREATE TABLE Kandidat
(
    id                   VARCHAR2(36)  NOT NULL,
    kandidatID           VARCHAR2(255) NOT NULL,
    nennungsNummer       NUMBER(10) NOT NULL,
    wahlvorschlag_id     VARCHAR2(255) NOT NULL,
    discarded            NUMBER(1) NOT NULL,
    votesByVoter         NUMBER(10),
    invalidVotes         NUMBER(10),
    votesByWahlvorschlag NUMBER(10),
    PRIMARY KEY (id),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (wahlvorschlag_id)
        REFERENCES Wahlvorschlag (id) ON DELETE CASCADE,
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (wahlvorschlag_id, kandidatID, nennungsNummer)
);
