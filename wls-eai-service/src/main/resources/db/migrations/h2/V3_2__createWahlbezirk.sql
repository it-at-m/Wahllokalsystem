CREATE TABLE wahlbezirk
(
    id            VARCHAR2(36) NOT NULL,
    wahlbezirkArt VARCHAR2(3)  NOT NULL,
    nummer        VARCHAR2(36) NOT NULL,
    a1            BIGINT       NOT NULL,
    a2            BIGINT       NOT NULL,
    a3            BIGINT       NOT NULL,

    wahlID        VARCHAR2(36) NOT NULL,

    FOREIGN KEY (wahlID) REFERENCES wahl (id),

    PRIMARY KEY (id)
)