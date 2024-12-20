CREATE TABLE Stimmzettelumschlaege
(
    wahlID                      VARCHAR(1024) NOT NULL,
    wahlbezirkID                VARCHAR(1024) NOT NULL,
    urneneroeffnungsUhrzeit     DATETIME,
    anzahlWaehler               BIGINT NOT NULL,
    anzahlWaehler2              BIGINT,

    PRIMARY KEY (wahlID, wahlbezirkID)
);