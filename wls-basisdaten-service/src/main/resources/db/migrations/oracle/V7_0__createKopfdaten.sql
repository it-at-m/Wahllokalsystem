CREATE TABLE Kopfdaten
(
    wahlID                   VARCHAR(255) NOT NULL,
    wahlbezirkID             VARCHAR(255) NOT NULL,
    gemeinde                 VARCHAR(255) NOT NULL,
    stimmzettelgebietsart    VARCHAR(255) NOT NULL,
    stimmzettelgebietsnummer VARCHAR(255) NOT NULL,
    stimmzettelgebietsname   VARCHAR(255) NOT NULL,
    wahlname                 VARCHAR(255) NOT NULL,
    wahlbezirknummer         VARCHAR(255) NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)
);