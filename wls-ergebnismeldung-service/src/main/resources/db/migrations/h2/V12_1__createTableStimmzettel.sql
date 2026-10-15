CREATE TABLE Stimmzettel
(
    wahlID                                VARCHAR(1024) NOT NULL,
    wahlbezirkID                          VARCHAR(1024) NOT NULL,
    teamID                                VARCHAR(1024) NOT NULL,
    stimmzettelkennung                    BIGINT        NOT NULL,

    selectedWahlvorschlaegeOrdnungszahlen VARCHAR(1024),

    PRIMARY KEY (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);

CREATE TABLE Stimmzettel_Kandidat
(
    fk_wahlID             VARCHAR(1024) NOT NULL,
    fk_wahlbezirkID       VARCHAR(1024) NOT NULL,
    fk_teamID             VARCHAR(1024) NOT NULL,
    fk_stimmzettelkennung BIGINT        NOT NULL,

    kandidatId            VARCHAR(1024) NOT NULL,
    isDiscarded           BOOLEAN       NOT NULL,
    votesByVoter          BIGINT        NOT NULL,

    CONSTRAINT fk_Stimmzettel
        FOREIGN KEY (fk_wahlbezirkID, fk_wahlID, fk_teamID, fk_stimmzettelkennung)
            REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);
