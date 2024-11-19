CREATE TABLE AWerte
(
    wahlID       VARCHAR(1024)  NOT NULL,
    wahlbezirkID VARCHAR(255)   NOT NULL,
    a1           NUMBER(19, 0)  NOT NULL,
    a2           NUMBER(19, 0),

    PRIMARY KEY (wahlID, wahlbezirkID)
);