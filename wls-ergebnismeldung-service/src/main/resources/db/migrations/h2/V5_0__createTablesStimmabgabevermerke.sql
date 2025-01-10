CREATE TABLE Stimmabgabevermerke
(
    waehlerverzeichnisNummer BIGINT        NOT NULL,
    wahlbezirkid             VARCHAR(1024) NOT NULL,
    anzahlblaetter           BIGINT        NOT NULL,

    PRIMARY KEY (waehlerverzeichnisNummer, wahlbezirkid)
);

CREATE TABLE Wahldaten
(
    id                       VARCHAR(36),
    wahlid                   VARCHAR(1024) NOT NULL,
    waehlerverzeichnisNummer BIGINT,
    wahlbezirkid             VARCHAR(1024),

    CONSTRAINT fk_Wd
        FOREIGN KEY (waehlerverzeichnisNummer, wahlbezirkid)
            REFERENCES Stimmabgabevermerke (waehlerverzeichnisNummer, wahlbezirkid)
            ON DELETE CASCADE,

    PRIMARY KEY (id)
);

CREATE TABLE EingenommeneWahlscheine
(
    wahldatenID    VARCHAR(36),
    anzahl         BIGINT       NOT NULL,
    stimmzettelart VARCHAR(255) NOT NULL,

    CONSTRAINT fk_Wahldaten_ew
        FOREIGN KEY (wahldatenID)
            REFERENCES Wahldaten (id)
            ON DELETE CASCADE
);

CREATE TABLE Vermerk
(
    id          VARCHAR(36),
    wahldatenID VARCHAR(36),
    blattnummer BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_Wahldaten
        FOREIGN KEY (wahldatenID)
            REFERENCES Wahldaten (id)
            ON DELETE CASCADE
);

CREATE TABLE Stimmzettel
(
    vermerkID      VARCHAR(36),
    anzahl         BIGINT       NOT NULL,
    stimmzettelart VARCHAR(255) NOT NULL,

    CONSTRAINT fk_Vermerke
        FOREIGN KEY (vermerkID)
            REFERENCES Vermerk (id)
);