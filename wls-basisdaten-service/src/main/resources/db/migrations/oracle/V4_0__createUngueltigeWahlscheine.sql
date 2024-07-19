CREATE TABLE Ungueltigews
(
    wahltagID      VARCHAR(1024) NOT NULL,
    wahlbezirksart VARCHAR(255)  NOT NULL,
    ungueltigews   BLOB          NOT NULL,

    PRIMARY KEY (wahltagID, wahlbezirksart)
);
