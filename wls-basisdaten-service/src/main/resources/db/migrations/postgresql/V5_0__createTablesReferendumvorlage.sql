CREATE TABLE referendumvorlagen
(
    id                  VARCHAR(36)   NOT NULL,
    wahlID              VARCHAR(1000) NOT NULL,
    wahlbezirkID        VARCHAR(1000) NOT NULL,
    stimmzettelgebietID VARCHAR(1000) NOT NULL,

    UNIQUE (wahlID, wahlbezirkID),

    PRIMARY KEY (id)

);

CREATE TABLE referendumvorlage
(
    id                   VARCHAR(36)   NOT NULL,
    wahlvorschlagID      VARCHAR(1000) NOT NULL,
    ordnungszahl         BIGINT        NOT NULL,
    kurzname             VARCHAR(1000) NOT NULL,
    frage                VARCHAR(1000) NOT NULL,

    referendumvorlagenID VARCHAR(36)   NOT NULL,


    FOREIGN KEY (referendumvorlagenID) REFERENCES referendumvorlagen (id),

    PRIMARY KEY (id)
);

CREATE TABLE referendumoption
(
    id                  VARCHAR(1000) NOT NULL,
    name                VARCHAR(1000) NOT NULL,
    position            BIGINT,
    referendumvorlageID VARCHAR(36)   NOT NULL,

    UNIQUE (id),
    FOREIGN KEY (referendumvorlageID) REFERENCES referendumvorlage (id)
);
