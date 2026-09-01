CREATE TABLE SystemBeschlussgrund
(
    id                             VARCHAR2(255) NOT NULL,
    reason                         VARCHAR2(100) NOT NULL,
    stimmzettel_wahlbezirkID       VARCHAR2(255) NOT NULL,
    stimmzettel_wahlID             VARCHAR2(255) NOT NULL,
    stimmzettel_teamID             VARCHAR2(255) NOT NULL,
    stimmzettel_stimmzettelkennung NUMBER(10) NOT NULL,
    CONSTRAINT pk_systemBeschlussgrund PRIMARY KEY (id),
    CONSTRAINT fk_systemBeschlussgrund_stimmzettel FOREIGN KEY (stimmzettel_wahlbezirkID, stimmzettel_wahlID,
                                                          stimmzettel_teamID, stimmzettel_stimmzettelkennung)
        REFERENCES Stimmzettel (wahlbezirkID, wahlID, teamID, stimmzettelkennung)
);