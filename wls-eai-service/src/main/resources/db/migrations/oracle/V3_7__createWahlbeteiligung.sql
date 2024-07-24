CREATE TABLE wahlbeteiligung
(
    id              VARCHAR2(36)   NOT NULL,
    wahlbezirkID    VARCHAR2(36)   NOT NULL,
    wahlID          VARCHAR2(36)   NOT NULL,
    anzahlWaehler   NUMBER         NOT NULL,
    meldeZeitpunkt  TIMESTAMP      NOT NULL,

    PRIMARY KEY (id)
);