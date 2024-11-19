CREATE TABLE AWerte
(
    wahlID       VARCHAR(255) NOT NULL,
    wahlbezirkID VARCHAR(255) NOT NULL,
    a1           BIGINT       NOT NULL,
    a2           BIGINT,

    PRIMARY KEY (wahlID, wahlbezirkID)
);