CREATE TABLE Handbuch
(
    wahltagID      VARCHAR(1024) NOT NULL,
    wahlbezirksart VARCHAR(255)  NOT NULL,
    handbuch       BLOB          NOT NULL,

    PRIMARY KEY (wahltagID, wahlbezirksart)
);
