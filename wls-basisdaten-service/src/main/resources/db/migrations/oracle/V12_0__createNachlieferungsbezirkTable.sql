CREATE TABLE Nachlieferungsbezirk
(
    wahltagID      VARCHAR(1024) NOT NULL,
    wahlbezirkID    VARCHAR(1024) NOT NULL,

    PRIMARY KEY (wahltagID, wahlbezirkID)
);