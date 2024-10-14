CREATE TABLE Wahl
(
    wahlID                      VARCHAR(1024) NOT NULL,
    name                        VARCHAR(255) NOT NULL,
    reihenfolge                 BIGINT NOT NULL,
    waehlerverzeichnisnummer    BIGINT NOT NULL,
    wahltag                     DATETIME NOT NULL,
    wahlart                     VARCHAR(255) NOT NULL,
    r                           BIGINT,
    g                           BIGINT,
    b                           BIGINT,
    nummer                      VARCHAR(255) NOT NULL,

    PRIMARY KEY (wahlID)
);