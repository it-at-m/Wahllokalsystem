DROP TABLE Stimmzettel_Kandidat;
DROP TABLE Stimmzettel;

CREATE TABLE Stimmzettel
(
    wahlbezirkID       VARCHAR(255) NOT NULL,
    wahlID             VARCHAR(255) NOT NULL,
    teamID             VARCHAR(255) NOT NULL,
    stimmzettelkennung INT          NOT NULL,
    invalideVotes      INT          NOT NULL,
    gueltigkeit        VARCHAR(255) NOT NULL,
    beschluss_pro      INT,
    beschluss_contra   INT,
    beschluss_text     VARCHAR(10000),
    PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Beschlussgrund
(
    id                    VARCHAR(255) NOT NULL,
    text                  VARCHAR(100) NOT NULL,
    fk_wahlbezirkID       VARCHAR(255) NOT NULL,
    fk_wahlID             VARCHAR(255) NOT NULL,
    fk_teamID             VARCHAR(255) NOT NULL,
    fk_stimmzettelkennung INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_beschlussgrund_stimmzettel FOREIGN KEY (fk_wahlbezirkID, fk_wahlID,
                                                          fk_teamID, fk_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Wahlvorschlag
(
    id                    VARCHAR(255) NOT NULL,
    wahlvorschlagID       VARCHAR(255) NOT NULL,
    selected              BOOLEAN      NOT NULL,
    fk_wahlbezirkID       VARCHAR(255) NOT NULL,
    fk_wahlID             VARCHAR(255) NOT NULL,
    fk_teamID             VARCHAR(255) NOT NULL,
    fk_stimmzettelkennung INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorschlag_stimmzettel FOREIGN KEY (fk_wahlbezirkID, fk_wahlID,
                                                         fk_teamID, fk_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Kandidat
(
    kandidatID           VARCHAR(255) NOT NULL,
    nennungsNummer       INT          NOT NULL,
    fk_wahlvorschlagID   VARCHAR(255) NOT NULL,
    discarded            BOOLEAN      NOT NULL,
    votesByVoter         INT,
    invalidVotes         INT,
    votesByWahlvorschlag INT,
    PRIMARY KEY (kandidatID, nennungsNummer),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (fk_wahlvorschlagID)
        REFERENCES Wahlvorschlag (id),
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (fk_wahlvorschlagID, kandidatID, nennungsNummer)
);
