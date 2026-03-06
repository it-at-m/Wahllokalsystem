CREATE TABLE WaehlerStimmzettel
(
    wahlbezirkID                          VARCHAR(1024) NOT NULL,
    wahlID                                VARCHAR(1024) NOT NULL,
    stimmzettelNummer                     NUMBER(19, 0) NOT NULL,

    selectedWahlvorschlaegeOrdnungszahlen VARCHAR(1024),

    PRIMARY KEY (wahlbezirkID, wahlID, stimmzettelNummer)
);

CREATE TABLE Stimmzettel_Kandidat
(
    fk_wahlbezirkID      VARCHAR(1024) NOT NULL,
    fk_wahlID            VARCHAR(1024) NOT NULL,
    fk_stimmzettelNummer NUMBER(19, 0) NOT NULL,

    kandidatId           VARCHAR(1024) NOT NULL,
    isDiscarded          NUMBER        NOT NULL,
    votesByVoter         NUMBER        NOT NULL,

    CONSTRAINT fk_Stimmzettel
        FOREIGN KEY (fk_wahlbezirkID, fk_wahlID, fk_stimmzettelNummer)
            REFERENCES WaehlerStimmzettel (wahlbezirkID, wahlID, stimmzettelNummer)
);
