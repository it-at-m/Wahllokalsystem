CREATE TABLE Waehleranzahl
(
    wahlID        VARCHAR(255) NOT NULL,
    wahlbezirkID  VARCHAR(255) NOT NULL,
    anzahlWaehler NUMBER       NOT NULL,
    uhrzeit       TIMESTAMP    NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)
);