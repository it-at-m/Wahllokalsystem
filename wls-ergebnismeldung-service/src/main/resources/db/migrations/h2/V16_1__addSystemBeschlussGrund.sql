CREATE TABLE SystemBeschlussgrund
(
    id                             VARCHAR(255) NOT NULL,
    reason                         VARCHAR(100) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR(255) NOT NULL,
    stimmzettel_teamID             VARCHAR(255) NOT NULL,
    stimmzettel_stimmzettelkennung INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_systemBeschlussgrund_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                                stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);