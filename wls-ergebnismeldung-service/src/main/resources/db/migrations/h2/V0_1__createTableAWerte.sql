CREATE TABLE AWerte
(
    wahlID       VARCHAR(1024)  NOT NULL,
    wahlbezirkID VARCHAR(255)   NOT NULL,
    a1           BIGINT(19, 0)  NOT NULL,
    a2           BIGINT(19, 0),

    PRIMARY KEY (wahlID, wahlbezirkID)
);