CREATE TABLE wahlvorschlaege
(
    id                  VARCHAR2(36) NOT NULL,
    wahlbezirkID        VARCHAR2(36) NOT NULL,
    wahlID              VARCHAR2(36) NOT NULL,
    stimmzettelgebietID VARCHAR2(36) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE wahlvorschlag
(
    id                  VARCHAR2(36)  NOT NULL,
    ordnungszahl        BIGINT        NOT NULL,
    kurzname            VARCHAR2(255) NOT NULL,
    erhaeltStimmen      BOOLEAN       NOT NULL,
    wahlvorschlaegeid   VARCHAR2(36)  NOT NULL,

    FOREIGN KEY (wahlvorschlaegeid) REFERENCES wahlvorschlaege (id),

    PRIMARY KEY (id)
);

CREATE TABLE kandidat
(
    id                            VARCHAR2(36)  NOT NULL,
    name                          VARCHAR2(255) NOT NULL,
    listenposition                BIGINT        NOT NULL,
    direktkandidat                BOOLEAN       NOT NULL,
    tabellenSpalteInNiederschrift BIGINT        NOT NULL,
    einzelbewerber                BOOLEAN       NOT NULL,
    wahlvorschlagID               VARCHAR2(36)  NOT NULL,

    FOREIGN KEY (wahlvorschlagID) REFERENCES wahlvorschlag (id),

    PRIMARY KEY (id)
);

CREATE TABLE referendumvorlagen
(
    id                    VARCHAR2(36) NOT NULL,
    stimmzettelgebietID   VARCHAR2(36) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE referendumvorlage
(
    id                    VARCHAR2(36)  NOT NULL,
    wahlvorschlagID       VARCHAR2(36)  NOT NULL,
    ordnungszahl          BIGINT        NOT NULL,
    kurzname              VARCHAR2(255) NOT NULL,
    frage                 VARCHAR2(255) NOT NULL,
    referendumvorlagenid  VARCHAR2(36)  NOT NULL,

    FOREIGN KEY (referendumvorlagenid) REFERENCES referendumvorlagen (id),

    PRIMARY KEY (id)
);

CREATE TABLE referendumoption
(
    id                    VARCHAR2(36) NOT NULL,
    name                  VARCHAR2(36) NOT NULL,
    position              BIGINT       NOT NULL,
    referendumvorlageid   VARCHAR2(36) NOT NULL,

    FOREIGN KEY (referendumvorlageid) REFERENCES referendumvorlage (id),

    PRIMARY KEY (id)
);