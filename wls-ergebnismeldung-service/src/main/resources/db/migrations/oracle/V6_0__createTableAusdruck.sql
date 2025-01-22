CREATE TABLE Ausdruck
(
    wahlID VARCHAR(255) NOT NULL,
    wahlbezirkID VARCHAR(255) NOT NULL,
    meldungsart VARCHAR(255) NOT NULL,
    content CLOB,
    erstellt_am TIMESTAMP NOT NULL,
    PRIMARY KEY (wahlID, wahlbezirkID, meldungsart)
);