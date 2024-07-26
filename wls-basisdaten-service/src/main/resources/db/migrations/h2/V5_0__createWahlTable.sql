CREATE TABLE wahl
(
    wahlID                          VARCHAR(1024) NOT NULL,
    name                            VARCHAR(1024) NOT NULL,
    waehlerverzeichnisnummer        BIGINT,
    reihenfolge                     BIGINT,
    wahltag                         TIMESTAMP NOT NULL,
    wahlart                         VARCHAR(4) NOT NULL,
    farbe                           VARCHAR(1),
    nummer                          BIGINT,

    PRIMARY KEY (wahlID)
);