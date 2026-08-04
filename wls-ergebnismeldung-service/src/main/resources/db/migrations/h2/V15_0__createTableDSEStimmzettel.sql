DROP TABLE Stimmzettel_Kandidat;
DROP TABLE Stimmzettel;

CREATE TABLE Stimmzettel
(
    wahlbezirkID       VARCHAR(255) NOT NULL,
    wahlID             VARCHAR(255) NOT NULL,
    teamID             VARCHAR(255) NOT NULL,
    stimmzettelkennung INT          NOT NULL,
    valid              BOOLEAN      NOT NULL,
    invalideVotes      INT          NOT NULL,
    gueltigkeit        VARCHAR(255) NOT NULL,
    beschluss_pro      INT,
    beschluss_contra   INT,
    beschluss_text     VARCHAR(4000),
    PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Beschlussvormerkung
(
    id                             VARCHAR(255)  NOT NULL,
    text                           VARCHAR(4000) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255)  NOT NULL,
    stimmzettel_wahlID             VARCHAR(255)  NOT NULL,
    stimmzettel_teamID             VARCHAR(255)  NOT NULL,
    stimmzettel_stimmzettelkennung INT           NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_beschlussvormerkung_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                               stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Wahlvorschlag
(
    id                             VARCHAR(255) NOT NULL,
    wahlvorschlagID                VARCHAR(255) NOT NULL,
    selected                       BOOLEAN      NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR(255) NOT NULL,
    stimmzettel_teamID             VARCHAR(255) NOT NULL,
    stimmzettel_stimmzettelkennung INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorschlag_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                         stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Kandidat
(
    kandidatID           VARCHAR(255) NOT NULL,
    nennungsNummer       INT          NOT NULL,
    wahlvorschlag_id     VARCHAR(255) NOT NULL,
    discarded            BOOLEAN      NOT NULL,
    votesByVoter         INT,
    invalidVotes         INT,
    votesByWahlvorschlag INT,
    PRIMARY KEY (kandidatID, nennungsNummer),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (wahlvorschlag_id)
        REFERENCES Wahlvorschlag (id),
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (wahlvorschlag_id, kandidatID, nennungsNummer)
);
