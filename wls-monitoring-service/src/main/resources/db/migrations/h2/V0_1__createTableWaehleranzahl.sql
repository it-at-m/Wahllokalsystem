CREATE TABLE Waehleranzahl
(
    wahlID        VARCHAR(255) NOT NULL,
    wahlbezirkID  VARCHAR(255) NOT NULL,
    anzahlWaehler BIGINT       NOT NULL,
    uhrzeit       DATETIME     NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)
);