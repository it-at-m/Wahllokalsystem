CREATE TABLE StimmzettelerfassungStatus
(
    wahlID       VARCHAR(1024)  NOT NULL,
    wahlbezirkID VARCHAR(255)   NOT NULL,
    status VARCHAR(255)  NOT NULL,

    PRIMARY KEY (wahlID, wahlbezirkID)
);