CREATE TABLE wahlvorschlaege
(
    id                  VARCHAR(255) NOT NULL,
    stimmzettelgebietid VARCHAR(255) NOT NULL,
    wahlID              VARCHAR(255) NOT NULL,
    wahlbezirkID        VARCHAR(255) NOT NULL,

    unique (wahlID, wahlbezirkID),

    PRIMARY KEY (id)
);

CREATE TABLE Wahlvorschlag
(
    id                VARCHAR(255) NOT NULL,
    identifikator     VARCHAR(255) NOT NULL,
    ordnungszahl      NUMBER       NOT NULL,
    kurzname          VARCHAR(255) NOT NULL,
    erhaeltStimmen    NUMBER       NOT NULL,
    wahlvorschlaegeID VARCHAR(255) NOT NULL,

    unique (identifikator),

    foreign key (wahlvorschlaegeID) REFERENCES wahlvorschlaege (id),

    primary key (id)
);

CREATE TABLE Kandidat
(
    id                            VARCHAR(255) NOT NULL,
    identifikator                 VARCHAR(255) NOT NULL,
    name                          VARCHAR(255) NOT NULL,
    listenposition                NUMBER       NOT NULL,
    direktkandidat                NUMBER       NOT NULL,
    tabellenSpalteInNiederschrift NUMBER       NOT NULL,
    einzelbewerber                NUMBER       NOT NULL,
    wahlvorschlagID               VARCHAR(255) NOT NULL,

    unique (identifikator),

    foreign key (wahlvorschlagID) REFERENCES Wahlvorschlag (id),

    primary key (id)
)