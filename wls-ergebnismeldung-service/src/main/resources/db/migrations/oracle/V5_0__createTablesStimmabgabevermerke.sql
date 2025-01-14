CREATE TABLE Stimmabgabevermerke
(
    waehlerverzeichnisNummer NUMBER(19, 0) NOT NULL,
    wahlbezirkID             VARCHAR(1024) NOT NULL,
    anzahlblaetter           NUMBER(19, 0) NOT NULL,

    PRIMARY KEY (waehlerverzeichnisNummer, wahlbezirkID)
);

CREATE TABLE Wahldaten
(
    id                       VARCHAR(36),
    wahlID                   VARCHAR(1024) NOT NULL,
    waehlerverzeichnisNummer NUMBER(19, 0),
    wahlbezirkID             VARCHAR(1024),

    CONSTRAINT fk_Wd
        FOREIGN KEY (waehlerverzeichnisNummer, wahlbezirkID)
            REFERENCES Stimmabgabevermerke (waehlerverzeichnisNummer, wahlbezirkID)
                ON DELETE CASCADE,

    PRIMARY KEY (id)
);

CREATE TABLE EingenommeneWahlscheine
(
    wahldatenID    VARCHAR(36),
    anzahl         NUMBER(19, 0) NOT NULL,
    stimmzettelart VARCHAR(255)  NOT NULL,

    CONSTRAINT fk_Wahldaten_ew
        FOREIGN KEY (wahldatenID)
            REFERENCES Wahldaten (id)
                ON DELETE CASCADE
);

CREATE TABLE Vermerk
(
    id          VARCHAR(36),
    wahldatenID VARCHAR(36),
    blattnummer NUMBER(19, 0) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_Wahldaten
        FOREIGN KEY (wahldatenID)
            REFERENCES Wahldaten (id)
                ON DELETE CASCADE
);

CREATE TABLE Stimmzettel
(
    vermerkID      VARCHAR(36),
    anzahl         NUMBER(19, 0) NOT NULL,
    stimmzettelart VARCHAR(255)  NOT NULL,

    CONSTRAINT fk_Vermerke
        FOREIGN KEY (vermerkID)
            REFERENCES Vermerk (id)
);