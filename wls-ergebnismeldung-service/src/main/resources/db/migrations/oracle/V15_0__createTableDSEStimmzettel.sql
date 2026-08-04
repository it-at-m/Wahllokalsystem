DROP TABLE Stimmzettel_Kandidat;
DROP TABLE Stimmzettel;

CREATE TABLE Stimmzettel (
    wahlbezirkID VARCHAR2(255) NOT NULL,
    wahlID VARCHAR2(255) NOT NULL,
    teamID VARCHAR2(255) NOT NULL,
    stimmzettelkennung NUMBER(10) NOT NULL,
    valid NUMBER(1) NOT NULL,
    invalideVotes NUMBER(10) NOT NULL,
    gueltigkeit VARCHAR2(255) NOT NULL,
    beschluss_pro NUMBER(10),
    beschluss_contra NUMBER(10),
    beschluss_text VARCHAR2(4000),
    CONSTRAINT pk_stimmzettel PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Beschlussvormerkung (
    id VARCHAR2(255) NOT NULL,
    text VARCHAR2(4000) NOT NULL,
    stimmzettel_wahlbezirkID VARCHAR2(255) NOT NULL,
    stimmzettel_wahlID VARCHAR2(255) NOT NULL,
    stimmzettel_teamID VARCHAR2(255) NOT NULL,
    stimmzettel_stimmzettelkennung NUMBER(10) NOT NULL,
    CONSTRAINT pk_beschlussvormerkung PRIMARY KEY (id),
    CONSTRAINT fk_beschlussvormerkung_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID, stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Wahlvorschlag (
    id VARCHAR2(255) NOT NULL,
    wahlvorschlagID VARCHAR2(255) NOT NULL,
    selected NUMBER(1) NOT NULL,
    stimmzettel_wahlbezirkID VARCHAR2(255) NOT NULL,
    stimmzettel_wahlID VARCHAR2(255) NOT NULL,
    stimmzettel_teamID VARCHAR2(255) NOT NULL,
    stimmzettel_stimmzettelkennung NUMBER(10) NOT NULL,
    CONSTRAINT pk_wahlvorschlag PRIMARY KEY (id),
    CONSTRAINT fk_wahlvorschlag_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID, stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Kandidat (
    kandidatID VARCHAR2(255) NOT NULL,
    nennungsNummer NUMBER(10) NOT NULL,
    wahlvorschlag_id VARCHAR2(255) NOT NULL,
    discarded NUMBER(1) NOT NULL,
    votesByVoter NUMBER(10),
    invalidVotes NUMBER(10),
    votesByWahlvorschlag NUMBER(10),
    CONSTRAINT pk_kandidat PRIMARY KEY (kandidatID, nennungsNummer),
    CONSTRAINT fk_kandidat_wahlvorschlag FOREIGN KEY (wahlvorschlag_id)
        REFERENCES Wahlvorschlag (id),
    CONSTRAINT uq_kandidat_per_wahlvorschlag UNIQUE (wahlvorschlag_id, kandidatID, nennungsNummer)
);
