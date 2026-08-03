CREATE TABLE DSEStimmzettel
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

CREATE TABLE DSEBeschlussvormerkung
(
    id                             VARCHAR(255)  NOT NULL,
    text                           VARCHAR(4000) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255)  NOT NULL,
    stimmzettel_wahlID             VARCHAR(255)  NOT NULL,
    stimmzettel_teamID             VARCHAR(255)  NOT NULL,
    stimmzettel_stimmzettelkennung INT           NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dsebeschlussvormerkung_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                                  stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES DSEStimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE DSEWahlvorschlag
(
    id                             VARCHAR(255) NOT NULL,
    wahlvorschlagID                VARCHAR(255) NOT NULL,
    selected                       BOOLEAN      NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR(255) NOT NULL,
    stimmzettel_teamID             VARCHAR(255) NOT NULL,
    stimmzettel_stimmzettelkennung INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_dsewahlvorschlag_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                            stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES DSEStimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE DSEKandidat
(
    kandidatID           VARCHAR(255) NOT NULL,
    nennungsNummer       INT          NOT NULL,
    wahlvorschlag_id     VARCHAR(255) NOT NULL,
    discarded            BOOLEAN      NOT NULL,
    votesByVoter         INT,
    invalidVotes         INT,
    votesByWahlvorschlag INT,
    PRIMARY KEY (kandidatID, nennungsNummer),
    CONSTRAINT fk_dsekandidat_wahlvorschlag FOREIGN KEY (wahlvorschlag_id)
        REFERENCES DSEWahlvorschlag (id),
    CONSTRAINT uq_dsekandidat_per_wahlvorschlag UNIQUE (wahlvorschlag_id, kandidatID, nennungsNummer)
);
