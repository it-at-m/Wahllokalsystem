CREATE TABLE Kopfdaten
(
    id                       VARCHAR(255) NOT NULL,
    wahlID                   VARCHAR(255) NOT NULL,
    wahlbezirkID             VARCHAR(255) NOT NULL,
    gemeinde                 VARCHAR(255) NOT NULL,
    stimmzettelgebietsart    VARCHAR(255) NOT NULL,
    stimmzettelgebietsnummer VARCHAR(255) NOT NULL,
    stimmzettelgebietsname   VARCHAR(255) NOT NULL,
    wahlname                 VARCHAR(255) NOT NULL,
    wahlbezirknummer         VARCHAR(255) NOT NULL,

    unique (wahlID, wahlbezirkID),

    PRIMARY KEY (id)
);