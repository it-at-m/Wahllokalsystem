CREATE TABLE wahltag
(
    id                  VARCHAR(255) NOT NULL,
    wahltagID           VARCHAR(1024) NOT NULL,
    wahltag             TIMESTAMP NOT NULL,
    beschreibung        VARCHAR(1024) NOT NULL,
    nummer              VARCHAR(1024),

    PRIMARY KEY (id)
);