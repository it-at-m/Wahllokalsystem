CREATE TABLE Ausdruck
(
    wahlID varchar(255) NOT NULL,
    wahlbezirkID varchar(255) NOT NULL,
    meldungsart varchar(255) NOT NULL,
    content CLOB,
    erstellt_am TIMESTAMP NOT NULL,
    PRIMARY KEY (wahlID, wahlbezirkID, meldungsart)
);