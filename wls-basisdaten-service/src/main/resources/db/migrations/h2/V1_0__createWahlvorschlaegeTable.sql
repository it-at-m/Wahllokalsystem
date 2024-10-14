CREATE TABLE Wahlvorschlaege
(
    id                  VARCHAR(255) NOT NULL,
    stimmzettelgebietID VARCHAR(255) NOT NULL,
    wahlID              VARCHAR(255) NOT NULL,
    wahlbezirkID        VARCHAR(255) NOT NULL,

    UNIQUE (wahlID, wahlbezirkID),

    PRIMARY KEY (id)
);

CREATE TABLE Wahlvorschlag
(
    id                VARCHAR(255) NOT NULL,
    identifikator     VARCHAR(255) NOT NULL,
    ordnungszahl      BIGINT       NOT NULL,
    kurzname          VARCHAR(255) NOT NULL,
    erhaeltStimmen    BOOLEAN      NOT NULL,
    wahlvorschlaegeID VARCHAR(255) NOT NULL,

    UNIQUE (identifikator),

    FOREIGN KEY (wahlvorschlaegeID) REFERENCES Wahlvorschlaege(id),

    PRIMARY KEY (id)
);

CREATE TABLE Kandidat
(
    id                            VARCHAR(255) NOT NULL,
    identifikator                 VARCHAR(255) NOT NULL,
    name                          VARCHAR(255) NOT NULL,
    listenposition                BIGINT       NOT NULL,
    direktkandidat                BOOLEAN      NOT NULL,
    tabellenSpalteInNiederschrift BIGINT       NOT NULL,
    einzelbewerber                BOOLEAN      NOT NULL,
    wahlvorschlagID               VARCHAR(255) NOT NULL,

    UNIQUE (identifikator),

    FOREIGN KEY (wahlvorschlagID) REFERENCES Wahlvorschlag(id),

    PRIMARY KEY (id)
)