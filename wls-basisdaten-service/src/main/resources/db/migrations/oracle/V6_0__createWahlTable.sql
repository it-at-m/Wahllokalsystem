create table Wahl
(
    wahlID                      VARCHAR(1024) NOT NULL,
    name                        VARCHAR(255) NOT NULL,
    reihenfolge                 NUMBER(19, 0) NOT NULL,
    waehlerverzeichnisnummer    NUMBER(19, 0) NOT NULL,
    wahltag                     TIMESTAMP  NOT NULL,
    wahlart                     VARCHAR(255) NOT NULL,
    r                           NUMBER(19, 0),
    g                           NUMBER(19, 0),
    b                           NUMBER(19, 0),
    nummer                      VARCHAR(255) NOT NULL,

    PRIMARY KEY (wahlID)
);