CREATE TABLE referendumvorlagen
(
    wahlID              VARCHAR2(1000) NOT NULL,
    wahlbezirkID        VARCHAR2(1000) NOT NULL,
    stimmzettelgebietID VARCHAR2(1000) NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)

);

CREATE TABLE referendumvorlage
(
    id              VARCHAR2(36)   NOT NULL,
    wahlvorschlagID VARCHAR2(1000) NOT NULL,
    ordnungszahl    BIGINT         NOT NULL,
    kurzname        VARCHAR2(1000) NOT NULL,
    frage           VARCHAR2(1000) NOT NULL,

    wahlID          VARCHAR2(1000) NOT NULL,
    wahlbezirkID    VARCHAR2(1000) NOT NULL,

    FOREIGN KEY (wahlID, wahlbezirkID) REFERENCES referendumvorlagen (wahlID, wahlbezirkID),

    PRIMARY KEY (id)
);

CREATE TABLE referendumoption
(
    id                  VARCHAR2(1000) NOT NULL,
    name                VARCHAR2(1000) NOT NULL,
    position            BIGINT,
    referendumvorlageID VARCHAR2(36)   NOT NULL,

    UNIQUE (id),
    FOREIGN KEY (referendumvorlageID) REFERENCES referendumvorlage (id)
);