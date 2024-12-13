CREATE TABLE Wahlscheine
(
    wahlID                           VARCHAR(1024) NOT NULL,
    wahlbezirkID                     VARCHAR(1024) NOT NULL,
    stimmabgabevermerke              BIGINT       NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)
);