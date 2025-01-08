CREATE TABLE Stimmzettelumschlaege
(
    wahlID                      VARCHAR(1024) NOT NULL,
    wahlbezirkID                VARCHAR(1024) NOT NULL,
    urneneroeffnungsUhrzeit     TIMESTAMP,
    anzahlWaehler               NUMBER(19, 0) NOT NULL,
    anzahlWaehler2              NUMBER(19, 0),

    PRIMARY KEY (wahlID, wahlbezirkID)
);