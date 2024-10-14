CREATE TABLE Wahltag
(
    wahltagID           VARCHAR(1024) NOT NULL,
    wahltag             TIMESTAMP NOT NULL,
    beschreibung        VARCHAR(1024),
    nummer              VARCHAR(1024),

    PRIMARY KEY (wahltagID)
);