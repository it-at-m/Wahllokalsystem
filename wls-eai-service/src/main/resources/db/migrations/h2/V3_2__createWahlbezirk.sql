CREATE TABLE wahlbezirk
(
    id            VARCHAR2(36) NOT NULL,
    wahlbezirkArt VARCHAR2(3)  NOT NULL,
    nummer        VARCHAR2(36) NOT NULL,

    wahlID        VARCHAR2(36) NOT NULL,

    FOREIGN KEY (wahlID) REFERENCES wahl (id),

    PRIMARY KEY (id)
)