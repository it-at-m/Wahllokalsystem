CREATE TABLE Wahlscheine
(
    wahlID                           VARCHAR(1024) NOT NULL,
    wahlbezirkID                     VARCHAR(1024) NOT NULL,
    stimmabgabevermerke              NUMBER(19, 0) NOT NULL

    PRIMARY KEY (wahlID, wahlbezirkID)
);