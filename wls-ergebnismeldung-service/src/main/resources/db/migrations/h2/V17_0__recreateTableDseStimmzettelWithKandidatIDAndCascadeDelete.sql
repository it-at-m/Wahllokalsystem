DROP TABLE Wahlvorstandbeschlussgrund;
DROP TABLE Systembeschlussgrund;
DROP TABLE Kandidat;
DROP TABLE Wahlvorschlag;
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
    beschluss_text     CLOB,
    CONSTRAINT pk_stimmzettel PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE WahlvorstandBeschlussgrund
(
    id                             VARCHAR(255) NOT NULL,
    text                           VARCHAR(100) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR(255) NOT NULL,
    stimmzettel_teamID             VARCHAR(255) NOT NULL,
    stimmzettel_stimmzettelkennung INT          NOT NULL,
    CONSTRAINT pk_wahlvorstandbeschlussgrund PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorstandbeschlussgrund_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID,
                                                                      stimmzettel_wahlID,
                                                                      stimmzettel_teamID,
                                                                      stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
        ON DELETE CASCADE
);

CREATE TABLE SystemBeschlussgrund
(
    id                             VARCHAR(255) NOT NULL,
    reason                         VARCHAR(100) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR(255) NOT NULL,
    stimmzettel_teamID             VARCHAR(255) NOT NULL,
    stimmzettel_stimmzettelkennung INT          NOT NULL,
    CONSTRAINT pk_systemBeschlussgrund PRIMARY KEY (id),
    CONSTRAINT fk_systemBeschlussgrund_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                                stimmzettel_teamID,
                                                                stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
        ON DELETE CASCADE
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
    CONSTRAINT pk_wahlvorschlag PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorschlag_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                         stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
        ON DELETE CASCADE
);

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
