DROP TABLE Kandidat;

TRUNCATE TABLE WAHLVORSCHLAG;
UPDATE Stimmzettel SET GUELTIGKEIT = 'LEER';

CREATE TABLE Kandidat
(
    id                   VARCHAR(36)  NOT NULL,
    kandidatID           VARCHAR(255) NOT NULL,
    nennungsNummer       INT          NOT NULL,
    wahlvorschlag_id     VARCHAR(255) NOT NULL,
    discarded            BOOLEAN      NOT NULL,
    votesByVoter         INT,
    invalidVotes         INT,
    votesByWahlvorschlag INT,
    PRIMARY KEY (id),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (wahlvorschlag_id)
        REFERENCES Wahlvorschlag (id) ON DELETE CASCADE,
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (wahlvorschlag_id, kandidatID, nennungsNummer)
);
