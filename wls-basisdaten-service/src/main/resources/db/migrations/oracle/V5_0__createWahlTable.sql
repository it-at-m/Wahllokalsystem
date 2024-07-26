CREATE TABLE wahl
(
    wahlID                          VARCHAR(1024) NOT NULL,
    name                            VARCHAR(1024) NOT NULL,
    waehlerverzeichnisnummer        number,
    reihenfolge                     number,
    wahltag                         TIMESTAMP NOT NULL,
    wahlart                         VARCHAR(4) NOT NULL,
    farbe                           VARCHAR(1),
    nummer                          number,

    PRIMARY KEY (wahlID)
);