CREATE TABLE Referendumvorlagen
(
    id                  VARCHAR2(36)   NOT NULL,
    wahlID              VARCHAR2(1000) NOT NULL,
    wahlbezirkID        VARCHAR2(1000) NOT NULL,
    stimmzettelgebietID VARCHAR2(1000) NOT NULL,

    UNIQUE (wahlID, wahlbezirkID),

    PRIMARY KEY (id)
);

CREATE TABLE Referendumvorlage
(
    id                   VARCHAR2(36)   NOT NULL,
    wahlvorschlagID      VARCHAR2(1000) NOT NULL,
    ordnungszahl         NUMBER         NOT NULL,
    kurzname             VARCHAR2(1000) NOT NULL,
    frage                VARCHAR2(1000) NOT NULL,

    referendumvorlagenID VARCHAR2(36)   NOT NULL,


    FOREIGN KEY (referendumvorlagenID) REFERENCES Referendumvorlagen(id),

    PRIMARY KEY (id)
);

CREATE TABLE Referendumoption
(
    id                  VARCHAR2(1000) NOT NULL,
    name                VARCHAR2(1000) NOT NULL,
    position            NUMBER,
    referendumvorlageID VARCHAR2(36)   NOT NULL,

    UNIQUE (id),
    FOREIGN KEY (referendumvorlageID) REFERENCES Referendumvorlage(id)
);