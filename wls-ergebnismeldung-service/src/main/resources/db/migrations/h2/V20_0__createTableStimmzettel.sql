CREATE TABLE WaehlerStimmzettel
(
    wahlbezirkID                          VARCHAR(1024) NOT NULL,
    wahlID                                VARCHAR(1024) NOT NULL,
    stimmzettelNummer                     BIGINT        NOT NULL,

    selectedWahlvorschlaegeOrdnungszahlen VARCHAR(1024),

    PRIMARY KEY (wahlbezirkID, wahlID, stimmzettelNummer)
);

CREATE TABLE Stimmzettel_Kandidat
(
    fk_wahlbezirkID      VARCHAR(1024) NOT NULL,
    fk_wahlID            VARCHAR(1024) NOT NULL,
    fk_stimmzettelNummer BIGINT        NOT NULL,

    kandidatId           VARCHAR(1024) NOT NULL,
    isDiscarded          BOOLEAN       NOT NULL,
    votesByVoter         BIGINT        NOT NULL,

    CONSTRAINT fk_Stimmzettel
        FOREIGN KEY (fk_wahlbezirkID, fk_wahlID, fk_stimmzettelNummer)
            REFERENCES WaehlerStimmzettel (wahlbezirkID, wahlID, stimmzettelNummer)
);
