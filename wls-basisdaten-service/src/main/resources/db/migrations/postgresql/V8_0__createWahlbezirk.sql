CREATE TABLE Wahlbezirk
(
    wahlbezirkID    VARCHAR(1024) NOT NULL,
    wahltag         TIMESTAMP NOT NULL,
    nummer          VARCHAR(255) NOT NULL,
    wahlbezirkart   VARCHAR(255) NOT NULL,
    wahlnummer      VARCHAR(255),
    wahlid          VARCHAR(255),

    PRIMARY KEY (wahlbezirkID)
);