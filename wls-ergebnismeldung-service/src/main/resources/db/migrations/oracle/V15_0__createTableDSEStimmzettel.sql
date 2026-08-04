DROP TABLE Stimmzettel_Kandidat;
DROP TABLE Stimmzettel;

CREATE TABLE Stimmzettel
(
    wahlbezirkID       VARCHAR2(255) NOT NULL,
    wahlID             VARCHAR2(255) NOT NULL,
    teamID             VARCHAR2(255) NOT NULL,
    stimmzettelkennung NUMBER(10) NOT NULL,
    invalideVotes      NUMBER(10) NOT NULL,
    gueltigkeit        VARCHAR2(255) NOT NULL,
    beschluss_pro      NUMBER(10),
    beschluss_contra   NUMBER(10),
    beschluss_text     VARCHAR2(10000),
    CONSTRAINT pk_stimmzettel PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Beschlussgrund
(
    id                    VARCHAR2(255) NOT NULL,
    text                  VARCHAR2(100) NOT NULL,
    fk_wahlbezirkID       VARCHAR2(255) NOT NULL,
    fk_wahlID             VARCHAR2(255) NOT NULL,
    fk_teamID             VARCHAR2(255) NOT NULL,
    fk_stimmzettelkennung NUMBER(10) NOT NULL,
    CONSTRAINT pk_beschlussgrund PRIMARY KEY (id),
    CONSTRAINT fk_beschlussgrund_stimmzettel FOREIGN KEY (fk_wahlbezirkID, fk_wahlID,
                                                          fk_teamID, fk_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Wahlvorschlag
(
    id                    VARCHAR2(255) NOT NULL,
    wahlvorschlagID       VARCHAR2(255) NOT NULL,
    selected              NUMBER(1) NOT NULL,
    fk_wahlbezirkID       VARCHAR2(255) NOT NULL,
    fk_wahlID             VARCHAR2(255) NOT NULL,
    fk_teamID             VARCHAR2(255) NOT NULL,
    fk_stimmzettelkennung NUMBER(10) NOT NULL,
    CONSTRAINT pk_wahlvorschlag PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorschlag_stimmzettel FOREIGN KEY (fk_wahlbezirkID, fk_wahlID,
                                                         fk_teamID, fk_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Kandidat
(
    kandidatID           VARCHAR2(255) NOT NULL,
    nennungsNummer       NUMBER(10) NOT NULL,
    fk_wahlvorschlagID   VARCHAR2(255) NOT NULL,
    discarded            NUMBER(1) NOT NULL,
    votesByVoter         NUMBER(10),
    invalidVotes         NUMBER(10),
    votesByWahlvorschlag NUMBER(10),
    CONSTRAINT pk_kandidat PRIMARY KEY (kandidatID, nennungsNummer),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (fk_wahlvorschlagID)
        REFERENCES Wahlvorschlag (id),
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (fk_wahlvorschlagID, kandidatID, nennungsNummer)
);
