CREATE TABLE wahlvorschlaege
(
    id                  VARCHAR(255) NOT NULL,
    stimmzettelgebietId VARCHAR(255) NOT NULL,
    wahlId              VARCHAR(255) NOT NULL,
    wahlbezirkId        VARCHAR(255) NOT NULL,

    UNIQUE (wahlId, wahlbezirkId),

    PRIMARY KEY (id)
);

CREATE TABLE wahlvorschlag
(
    id                VARCHAR(255) NOT NULL,
    identifikator     VARCHAR(255) NOT NULL,
    ordnungszahl      BIGINT       NOT NULL,
    kurzname          VARCHAR(255) NOT NULL,
    erhaeltStimmen    BOOLEAN      NOT NULL,
    wahlvorschlaegeId VARCHAR(255) NOT NULL,

    UNIQUE (identifikator),

    FOREIGN KEY (wahlvorschlaegeId) REFERENCES wahlvorschlaege (id),

    PRIMARY KEY (id)
);

CREATE TABLE kandidat
(
    id                            VARCHAR(255) NOT NULL,
    identifikator                 VARCHAR(255) NOT NULL,
    name                          VARCHAR(255) NOT NULL,
    listenposition                BIGINT       NOT NULL,
    direktkandidat                BOOLEAN      NOT NULL,
    tabellenSpalteInNiederschrift BIGINT       NOT NULL,
    einzelbewerber                BOOLEAN      NOT NULL,
    wahlvorschlagId               VARCHAR(255) NOT NULL,

    UNIQUE (identifikator),

    FOREIGN KEY (wahlvorschlagId) REFERENCES wahlvorschlag (id),

    PRIMARY KEY (id)
);
